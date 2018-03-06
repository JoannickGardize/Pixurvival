package com.pixurvival.core;

import java.nio.ByteBuffer;

import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.message.InventoryActionRequest;
import com.pixurvival.core.message.PlayerActionRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerEntity extends AliveEntity implements InventoryHolder {

	private String name;

	private Inventory inventory;

	private boolean extendedUpdateRequired = false;
	private @Setter ItemStack heldItemStack;

	public void apply(PlayerActionRequest actionRequest) {
		setMovingAngle(actionRequest.getDirection().getAngle());
		setForward(actionRequest.isForward());
	}

	public void apply(InventoryActionRequest actionRequest) {
		switch (actionRequest.getType()) {
		case CURSOR_MY_INVENTORY:
			if (inventory.isValidIndex(actionRequest.getSlotIndex())) {
				ItemStack currentContent = inventory.getSlot(actionRequest.getSlotIndex());
				if (heldItemStack != null && currentContent != null
						&& heldItemStack.getItem() == currentContent.getItem()) {
					int quantity = currentContent.getQuantity();
					heldItemStack.setQuantity(currentContent.addQuantity(heldItemStack.getQuantity()));
					if (quantity != currentContent.getQuantity()) {
						inventory.notifySlotChanged(actionRequest.getSlotIndex());
					}
					if (heldItemStack.getQuantity() == 0) {
						heldItemStack = null;
					}
				} else {
					inventory.setSlot(actionRequest.getSlotIndex(), heldItemStack);
					heldItemStack = currentContent;
				}
				extendedUpdateRequired = true;
			}
			break;
		}
	}

	@Override
	public void initialize() {
		if (getWorld().isServer()) {
			inventory = new Inventory(getInventorySize());
		}
	}

	@Override
	public void update() {
		super.update();
	}

	@Override
	public double getMaxHealth() {
		return 100;
	}

	@Override
	public double getSpeedPotential() {
		return 10;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.PLAYER;
	}

	@Override
	public double getBoundingRadius() {
		return 0.42;
	}

	public int getInventorySize() {
		return 32;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer) {
		// normal part
		buffer.putDouble(getPosition().x);
		buffer.putDouble(getPosition().y);
		buffer.putDouble(getHealth());
		buffer.putDouble(getMovingAngle());
		buffer.putDouble(getAimingAngle());
		buffer.put(isForward() ? (byte) 1 : (byte) 0);

		// extended part
		if (extendedUpdateRequired) {
			buffer.put((byte) 1);
			if (heldItemStack == null) {
				buffer.putShort((short) -1);
			} else {
				buffer.putShort(heldItemStack.getItem().getId());
				buffer.putShort((short) heldItemStack.getQuantity());
			}
		} else {
			buffer.put((byte) 0);
		}
	}

	@Override
	public void applyUpdate(ByteBuffer buffer) {
		// normal part
		getPosition().set(buffer.getDouble(), buffer.getDouble());
		setHealth(buffer.getDouble());
		setMovingAngle(buffer.getDouble());
		setAimingAngle(buffer.getDouble());
		setForward(buffer.get() == 1 ? true : false);

		// extended part
		if (buffer.get() == 1) {
			short itemId = buffer.getShort();
			if (itemId == -1) {
				heldItemStack = null;
			} else {
				heldItemStack = new ItemStack(getWorld().getContentPack().getItemsById().get(itemId),
						buffer.getShort());
			}
		}
	}
}
