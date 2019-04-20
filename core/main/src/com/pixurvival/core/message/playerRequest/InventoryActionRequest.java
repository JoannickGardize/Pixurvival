package com.pixurvival.core.message.playerRequest;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryActionRequest implements IPlayerActionRequest {

	@Getter
	public static enum Type {
		SWAP_CLICK_MY_INVENTORY,
		SPLIT_CLICK_MY_INVENTORY;
	}

	private Type type;
	private short slotIndex;

	@Override
	public void apply(PlayerEntity player) {
		PlayerInventory inventory = player.getInventory();
		if (!inventory.isValidIndex(slotIndex)) {
			Log.warn("Warning : invalid slot index : " + slotIndex);
			return;
		}
		switch (type) {
		case SWAP_CLICK_MY_INVENTORY:
			performNormalInventoryAction(player);
			break;
		case SPLIT_CLICK_MY_INVENTORY:
			ItemStack currentContent = inventory.getSlot(slotIndex);
			ItemStack heldItemStack = inventory.getHeldItemStack();
			if (heldItemStack != null && currentContent != null
					&& heldItemStack.getItem() == currentContent.getItem()) {
				if (currentContent.overflowingQuantity(1) == 0) {
					inventory.setSlot(slotIndex, currentContent.add(1));
					if (heldItemStack.getQuantity() == 1) {
						inventory.setHeldItemStack(null);
					} else {
						inventory.setHeldItemStack(heldItemStack.sub(1));
					}
				}
			} else if (heldItemStack != null && currentContent == null) {
				inventory.setSlot(slotIndex, new ItemStack(heldItemStack.getItem()));
				if (heldItemStack.getQuantity() == 1) {
					inventory.setHeldItemStack(null);
				} else {
					inventory.setHeldItemStack(heldItemStack.sub(1));
				}
			} else if (heldItemStack == null && currentContent != null) {
				int halfQuantity = currentContent.getQuantity() / 2 + (currentContent.getQuantity() % 2 == 0 ? 0 : 1);
				inventory.setHeldItemStack(new ItemStack(currentContent.getItem(), halfQuantity));
				if (currentContent.getQuantity() == halfQuantity) {
					inventory.setSlot(slotIndex, null);
				} else {
					inventory.setSlot(slotIndex, currentContent.sub(halfQuantity));
				}
			} else {
				performNormalInventoryAction(player);
			}
			break;
		}
	}

	private void performNormalInventoryAction(PlayerEntity player) {
		PlayerInventory inventory = player.getInventory();
		ItemStack currentContent = inventory.getSlot(slotIndex);
		ItemStack heldItemStack = inventory.getHeldItemStack();
		if (heldItemStack != null && currentContent != null && heldItemStack.getItem() == currentContent.getItem()) {
			int quantity = currentContent.getQuantity();
			int overflow = currentContent.overflowingQuantity(heldItemStack.getQuantity());
			inventory.setSlot(slotIndex, currentContent.copy(quantity + heldItemStack.getQuantity() - overflow));
			if (overflow == 0) {
				inventory.setHeldItemStack(null);
			} else {
				inventory.setHeldItemStack(heldItemStack.copy(overflow));
			}
		} else {
			inventory.setSlot(slotIndex, heldItemStack);
			inventory.setHeldItemStack(currentContent);
		}
	}

	@Override
	public boolean isClientPreapply() {
		return true;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<InventoryActionRequest> {

		@Override
		public void write(Kryo kryo, Output output, InventoryActionRequest object) {
			output.writeByte(object.type.ordinal());
			output.writeShort(object.slotIndex);
		}

		@Override
		public InventoryActionRequest read(Kryo kryo, Input input, Class<InventoryActionRequest> type) {
			return new InventoryActionRequest(Type.values()[input.readByte()], input.readShort());
		}

	}

}
