package com.pixurvival.core.contentPack.item;

import java.nio.ByteBuffer;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ItemStackEntity extends Entity {

	public static final double MAGNET_DISTANCE = 2;
	public static final double INHIBITION_DISTANCE = 2.5;
	public static final double RANDOM_SPAWN_RADIUS = 2;

	public static enum State {
		NORMAL,
		MAGNTIZED,
		INHIBITED,
		SPAWNING;
	}

	private @Getter ItemStack itemStack;
	private PlayerEntity magnetTarget = null;
	private @Getter State state;
	private Vector2 spawnTarget = new Vector2();

	public ItemStackEntity(ItemStack itemStack) {
		this.itemStack = itemStack;
		state = State.NORMAL;
	}

	public void spawnRandom() {
		spawnTarget.setFromEuclidean(getWorld().getRandom().nextDouble() * RANDOM_SPAWN_RADIUS,
				getWorld().getRandom().nextDouble() * Math.PI * 2);
		state = State.SPAWNING;
	}

	@Override
	public void initialize() {
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
			if (this.distanceSquared(magnetTarget) > INHIBITION_DISTANCE * INHIBITION_DISTANCE) {
				state = State.NORMAL;
				magnetTarget = null;
			}
			break;
		case NORMAL:
			if (getWorld().isServer()) {
				getWorld().getEntityPool().get(EntityGroup.PLAYER).forEach(p -> {
					if (distanceSquared(p) <= MAGNET_DISTANCE * MAGNET_DISTANCE) {
						magnetTarget = (PlayerEntity) p;
						state = State.MAGNTIZED;
					}
				});
			}
			break;
		case MAGNTIZED:
			setMovingAngle(angleTo(magnetTarget));
			setForward(true);
			if (collide(magnetTarget)) {
				setAlive(false);
				if (getWorld().isServer() && !magnetTarget.getInventory().smartAdd(itemStack)) {
					setAlive(true);
					state = State.INHIBITED;
					setForward(false);
				}
			}
			break;
		case SPAWNING:
			setForward(true);
			setMovingAngle(getPosition().angleTo(spawnTarget));
			double deltaSpeed = getSpeed() * getWorld().getTime().getDeltaTime();
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
		buffer.putShort(itemStack.getItem().getId());
		buffer.putShort((short) itemStack.getQuantity());
		buffer.putDouble(getPosition().x);
		buffer.putDouble(getPosition().y);
		buffer.put((byte) state.ordinal());
		switch (state) {
		case INHIBITED:
		case MAGNTIZED:
			buffer.putLong(magnetTarget.getId());
			break;
		case SPAWNING:
			buffer.putDouble(spawnTarget.x);
			buffer.putDouble(spawnTarget.y);
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
			itemStack = new ItemStack(getWorld().getContentPack().getItemsById().get(itemId), quantity);
		}
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		state = State.values()[buffer.get()];
		switch (state) {
		case INHIBITED:
		case MAGNTIZED:
			long magnetTargetId = buffer.getLong();
			magnetTarget = (PlayerEntity) getWorld().getEntityPool().get(EntityGroup.PLAYER, magnetTargetId);
			break;
		case SPAWNING:
			spawnTarget.set(buffer.getDouble(), buffer.getDouble());
			break;
		default:
			break;

		}
	}

	@Override
	public double getSpeedPotential() {
		return 15;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

}
