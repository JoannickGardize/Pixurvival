package com.pixurvival.core;

import java.nio.ByteBuffer;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.message.InventoryActionRequest;
import com.pixurvival.core.message.PlayerActionRequest;

import lombok.Getter;

@Getter
public class PlayerEntity extends AliveEntity implements InventoryHolder {

	private String name;

	private PlayerInventory inventory;

	private boolean extendedUpdateRequired = false;

	public void apply(PlayerActionRequest actionRequest) {
		setMovingAngle(actionRequest.getDirection().getAngle());
		setForward(actionRequest.isForward());
	}

	public void apply(InventoryActionRequest actionRequest) {
		if (!inventory.isValidIndex(actionRequest.getSlotIndex())) {
			Log.warn("Warning : invalid slot index : " + actionRequest.getSlotIndex());
			return;
		}
		switch (actionRequest.getType()) {
		case NORMAL_CLICK_MY_INVENTORY:
			performNormalInventoryAction(actionRequest.getSlotIndex());
			break;
		case SPECIAL_CLICK_MY_INVENTORY:
			ItemStack currentContent = inventory.getSlot(actionRequest.getSlotIndex());
			ItemStack heldItemStack = inventory.getHeldItemStack();
			if (heldItemStack != null && currentContent != null
					&& heldItemStack.getItem() == currentContent.getItem()) {
				if (currentContent.addQuantity(1) == 0) {
					heldItemStack.removeQuantity(1);
					inventory.notifySlotChanged(actionRequest.getSlotIndex());
					if (heldItemStack.getQuantity() == 0) {
						inventory.setHeldItemStack(null);
					}
				}
			} else if (heldItemStack != null && currentContent == null) {
				heldItemStack.removeQuantity(1);
				inventory.setSlot(actionRequest.getSlotIndex(), new ItemStack(heldItemStack.getItem()));
				if (heldItemStack.getQuantity() == 0) {
					inventory.setHeldItemStack(null);
				}
			} else if (heldItemStack == null && currentContent != null) {
				int halfQuantity = currentContent.getQuantity() / 2 + (currentContent.getQuantity() % 2 == 0 ? 0 : 1);
				halfQuantity = currentContent.removeQuantity(halfQuantity);
				inventory.setHeldItemStack(new ItemStack(currentContent.getItem(), halfQuantity));
				if (currentContent.getQuantity() == 0) {
					inventory.setSlot(actionRequest.getSlotIndex(), null);
				}
				inventory.notifySlotChanged(actionRequest.getSlotIndex());
			} else {
				performNormalInventoryAction(actionRequest.getSlotIndex());
			}
			break;
		}
	}

	@Override
	public void initialize() {
		if (getWorld().isServer()) {
			inventory = new PlayerInventory(getInventorySize());
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
		// if (extendedUpdateRequired) {
		// buffer.put((byte) 1);
		// } else {
		// buffer.put((byte) 0);
		// }
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
		// if (buffer.get() == 1) {
		// }
	}

	private void performNormalInventoryAction(int slotIndex) {
		ItemStack currentContent = inventory.getSlot(slotIndex);
		ItemStack heldItemStack = inventory.getHeldItemStack();
		if (heldItemStack != null && currentContent != null && heldItemStack.getItem() == currentContent.getItem()) {
			int quantity = currentContent.getQuantity();
			heldItemStack.setQuantity(currentContent.addQuantity(heldItemStack.getQuantity()));
			if (quantity != currentContent.getQuantity()) {
				inventory.notifySlotChanged(slotIndex);
			}
			if (heldItemStack.getQuantity() == 0) {
				inventory.setHeldItemStack(null);
			}
		} else {
			inventory.setSlot(slotIndex, heldItemStack);
			inventory.setHeldItemStack(currentContent);
		}
		extendedUpdateRequired = true;
	}
}
