package com.pixurvival.core.item;

import java.nio.ByteBuffer;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Timer;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ItemStackEntity extends Entity {

	public static final double MAGNET_DISTANCE = 2;
	public static final double INHIBITION_DISTANCE = 2.5;
	public static final double RANDOM_SPAWN_RADIUS = 2;
	public static final long SPEED_INTERPOLATION_DURATION = 200;
	public static final double START_SPEED = 2;
	public static final double END_SPEED = 15;

	public enum State {
		NORMAL,
		MAGNTIZED,
		INHIBITED,
		SPAWNING;
	}

	private @Getter ItemStack itemStack;
	private PlayerEntity magnetTarget = null;
	private @Getter State state;
	private float spawnProgress;
	private float spawnDistance;
	private Vector2 spawnTarget = new Vector2();
	private Timer speedInterpolation;

	public ItemStackEntity(ItemStack itemStack) {
		this.itemStack = itemStack;
		state = State.NORMAL;
	}

	public void spawn(double distance, double angle) {
		spawnProgress = 0;
		spawnDistance = (float) distance;
		spawnTarget.setFromEuclidean(distance, angle).add(getPosition());
		state = State.SPAWNING;
	}

	public void spawn(double angle) {
		spawn(INHIBITION_DISTANCE, angle);
	}

	public void spawnRandom() {
		spawn(getWorld().getRandom().nextDouble() * RANDOM_SPAWN_RADIUS, getWorld().getRandom().nextDouble() * Math.PI * 2);
	}

	@Override
	public void initialize() {
		speedInterpolation = new Timer(getWorld(), SPEED_INTERPOLATION_DURATION, false);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ITEM_STACK;
	}

	@Override
	public double getBoundingRadius() {
		return 0.1;
	}

	@Override
	public void update() {
		switch (state) {
		case INHIBITED:
			setForward(false);
			if (this.distanceSquared(magnetTarget) > INHIBITION_DISTANCE * INHIBITION_DISTANCE) {
				state = State.NORMAL;
				magnetTarget = null;
			}
			break;
		case NORMAL:
			setForward(false);
			if (getWorld().isServer()) {
				getWorld().getEntityPool().get(EntityGroup.PLAYER).forEach(p -> {
					if (distanceSquared(p) <= MAGNET_DISTANCE * MAGNET_DISTANCE) {
						magnetTarget = (PlayerEntity) p;
						state = State.MAGNTIZED;
						speedInterpolation.reset();
					}
				});
			}
			break;
		case MAGNTIZED:
			speedInterpolation.update(getWorld());
			setMovingAngle(angleTo(magnetTarget));
			setForward(true);
			if (collideDynamic(magnetTarget)) {
				setAlive(false);
				if (getWorld().isServer()) {
					ItemStack rest = magnetTarget.getInventory().add(itemStack);
					if (rest != null) {
						setAlive(true);
						state = State.INHIBITED;
						speedInterpolation.reset();
						setForward(false);
						itemStack = rest;
					}
				}
			}
			break;
		case SPAWNING:
			setForward(true);
			setMovingAngle(getPosition().angleTo(spawnTarget));
			double deltaSpeed = getSpeed() * getWorld().getTime().getDeltaTime();
			spawnProgress += deltaSpeed;
			if (getPosition().distanceSquared(spawnTarget) <= deltaSpeed * deltaSpeed) {
				getPosition().set(spawnTarget);
				setForward(false);
				state = State.NORMAL;
			}
			break;
		}
		super.update();
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		buffer.putShort((short) itemStack.getItem().getId());
		buffer.putShort((short) itemStack.getQuantity());
		buffer.putDouble(getPosition().x);
		buffer.putDouble(getPosition().y);
		buffer.put((byte) state.ordinal());
		switch (state) {
		case INHIBITED:
			buffer.putLong(magnetTarget.getId());
			break;
		case MAGNTIZED:
			buffer.putLong(magnetTarget.getId());
			buffer.putLong(speedInterpolation.getStartTimeMillis());
			break;
		case SPAWNING:
			buffer.putDouble(spawnTarget.x);
			buffer.putDouble(spawnTarget.y);
			buffer.putFloat(spawnProgress);
			buffer.putFloat(spawnDistance);
			break;
		default:
			break;

		}
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		short itemId = buffer.getShort();
		short quantity = buffer.getShort();
		if (itemStack == null) {
			itemStack = new ItemStack(getWorld().getContentPack().getItems().get(itemId), quantity);
		}
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		state = State.values()[buffer.get()];
		switch (state) {
		case INHIBITED:
			long magnetTargetId = buffer.getLong();
			magnetTarget = (PlayerEntity) getWorld().getEntityPool().get(EntityGroup.PLAYER, magnetTargetId);
			break;
		case MAGNTIZED:
			magnetTargetId = buffer.getLong();
			magnetTarget = (PlayerEntity) getWorld().getEntityPool().get(EntityGroup.PLAYER, magnetTargetId);
			speedInterpolation.setStartTimeMillis(buffer.getLong());
			break;
		case SPAWNING:
			spawnTarget.set(buffer.getDouble(), buffer.getDouble());
			spawnProgress = buffer.getFloat();
			spawnDistance = buffer.getFloat();
			break;
		default:
			break;

		}
	}

	@Override
	public double getSpeedPotential() {
		if (state == State.MAGNTIZED) {
			return MathUtils.linearInterpolate(START_SPEED, END_SPEED, speedInterpolation.getProgress());
		} else {
			return END_SPEED - (END_SPEED - START_SPEED) * (spawnProgress / spawnDistance);
		}
	}

	@Override
	public boolean isSolid() {
		return false;
	}

}
