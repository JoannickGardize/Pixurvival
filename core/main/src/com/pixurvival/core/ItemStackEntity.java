package com.pixurvival.core;

import java.nio.ByteBuffer;

import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ItemStackEntity extends Entity {

	private static double MAGNET_DISTANCE = 2;

	private @Getter ItemStack itemStack;
	private PlayerEntity magnetTarget;

	public ItemStackEntity(ItemStack itemStack) {
		this.itemStack = itemStack;
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
		if (magnetTarget == null && getWorld().isServer()) {
			getWorld().getEntityPool().get(EntityGroup.PLAYER).forEach(p -> {
				if (distanceSquared(p) <= MAGNET_DISTANCE * MAGNET_DISTANCE) {
					magnetTarget = (PlayerEntity) p;
					setForward(true);
				}
			});
		}
		if (magnetTarget != null) {
			setMovingAngle(angleTo(magnetTarget));
			if (collide(magnetTarget)) {
				// TODO manage full inventory
				setAlive(false);
				if (getWorld().isServer()) {
					magnetTarget.getInventory().smartAdd(itemStack);
				}
			}
		}
		super.update();
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		buffer.putShort(itemStack.getItem().getId());
		buffer.putShort((short) itemStack.getQuantity());
		buffer.putDouble(getPosition().x);
		buffer.putDouble(getPosition().y);
		if (magnetTarget == null) {
			buffer.putLong(-1);
		} else {
			buffer.putLong(magnetTarget.getId());
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
		long magnetTargetId = buffer.getLong();
		if (magnetTargetId == -1) {
			magnetTarget = null;
		} else {
			magnetTarget = (PlayerEntity) getWorld().getEntityPool().get(EntityGroup.PLAYER, magnetTargetId);
		}
	}

	@Override
	public double getSpeedPotential() {
		return 1;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

}
