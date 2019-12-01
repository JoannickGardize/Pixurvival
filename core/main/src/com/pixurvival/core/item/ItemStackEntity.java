package com.pixurvival.core.item;

import java.nio.ByteBuffer;

import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntitySearchResult;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Timer;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ItemStackEntity extends Entity {

	public static final float MAGNET_DISTANCE = 2;
	public static final float INHIBITION_DISTANCE = 2.5f;
	public static final float RANDOM_SPAWN_RADIUS = 2;
	public static final long SPEED_INTERPOLATION_DURATION = 200;
	public static final float START_SPEED = 2;
	public static final float END_SPEED = 15;

	public enum State {
		WAITING,
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
		state = State.WAITING;
	}

	public void spawn(float distance, float angle) {
		spawnProgress = 0;
		spawnDistance = distance;
		spawnTarget.setFromEuclidean(distance, angle).add(getPosition());
		state = State.SPAWNING;
	}

	public void spawn(float angle) {
		spawn(INHIBITION_DISTANCE, angle);
	}

	public static void spawn(World world, ItemStack[] items, Vector2 position) {
		for (ItemStack itemStack : items) {
			ItemStackEntity itemStackEntity = new ItemStackEntity(itemStack);
			itemStackEntity.getPosition().set(position.getX(), position.getY());
			world.getEntityPool().add(itemStackEntity);
			itemStackEntity.spawnRandom();
		}
	}

	public void spawnRandom() {
		spawn(getWorld().getRandom().nextFloat() * RANDOM_SPAWN_RADIUS, getWorld().getRandom().nextAngle());
	}

	@Override
	public void initialize() {
		super.initialize();
		speedInterpolation = new Timer(getWorld(), SPEED_INTERPOLATION_DURATION, false);
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ITEM_STACK;
	}

	@Override
	public float getCollisionRadius() {
		return 0.1f;
	}

	@Override
	public void update() {
		if (!isCollisionLock()) {
			switch (state) {
			case INHIBITED:
				setForward(false);
				if (this.distanceSquared(magnetTarget) > INHIBITION_DISTANCE * INHIBITION_DISTANCE) {
					state = State.WAITING;
					magnetTarget = null;
				}
				break;
			case WAITING:
				setForward(false);
				if (getWorld().isServer()) {
					EntitySearchResult result = findClosest(EntityGroup.PLAYER, MAGNET_DISTANCE);
					if (result.getDistanceSquared() <= MAGNET_DISTANCE * MAGNET_DISTANCE) {
						setStateChanged(true);
						magnetTarget = (PlayerEntity) result.getEntity();
						state = State.MAGNTIZED;
						speedInterpolation.reset();

					}
				}
				break;
			case MAGNTIZED:
				speedInterpolation.update(getWorld());
				setMovingAngle(angleToward(magnetTarget));
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
							setStateChanged(true);
						}
					}
				}
				break;
			case SPAWNING:
				setForward(true);
				setMovingAngle(getPosition().angleToward(spawnTarget));
				float deltaSpeed = getSpeed() * getWorld().getTime().getDeltaTime();
				spawnProgress += deltaSpeed;
				if (getPosition().distanceSquared(spawnTarget) <= deltaSpeed * deltaSpeed) {
					getPosition().set(spawnTarget);
					setForward(false);
					state = State.WAITING;
				}
				break;
			}
		}
		super.update();
	}

	@Override
	public void writeInitialization(ByteBuffer buffer) {
		buffer.putShort((short) itemStack.getItem().getId());
		buffer.putShort((short) itemStack.getQuantity());
	}

	@Override
	public void applyInitialization(ByteBuffer buffer) {
		short itemId = buffer.getShort();
		short quantity = buffer.getShort();
		if (itemStack == null) {
			itemStack = new ItemStack(getWorld().getContentPack().getItems().get(itemId), quantity);
		}
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, boolean full) {
		buffer.putFloat(getPosition().getX());
		buffer.putFloat(getPosition().getY());
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
			buffer.putFloat(spawnTarget.getX());
			buffer.putFloat(spawnTarget.getY());
			buffer.putFloat(spawnProgress);
			buffer.putFloat(spawnDistance);
			break;
		default:
			break;
		}
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		getPosition().set(buffer.getFloat(), buffer.getFloat());
		state = State.values()[buffer.get()];
		switch (state) {
		case INHIBITED:
			long magnetTargetId = buffer.getLong();
			magnetTarget = getWorld().getPlayerEntities().get(magnetTargetId);
			break;
		case MAGNTIZED:
			magnetTargetId = buffer.getLong();
			magnetTarget = getWorld().getPlayerEntities().get(magnetTargetId);
			speedInterpolation.setStartTimeMillis(buffer.getLong());
			break;
		case SPAWNING:
			spawnTarget.set(buffer.getFloat(), buffer.getFloat());
			spawnProgress = buffer.getFloat();
			spawnDistance = buffer.getFloat();
			break;
		default:
			break;

		}
	}

	@Override
	public float getSpeedPotential() {
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

	@Override
	protected void collisionLockEnded() {
		if (state == State.SPAWNING) {
			setForward(false);
			state = State.WAITING;
		}
	}

	@Override
	protected boolean antiCollisionLockEnabled() {
		return state == State.WAITING;
	}
}
