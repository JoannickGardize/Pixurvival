package com.pixurvival.core.aliveEntity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.item.AccessoryItem;
import com.pixurvival.core.item.ClothingItem;
import com.pixurvival.core.item.HandItem;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;

public class PlayerInventory extends Inventory {

	public static final int HELD_ITEM_STACK_INDEX = -1;
	public static final int ITEM_IN_HAND_INDEX = -2;
	public static final int CLOTHING_INDEX = -3;
	public static final int ACCESSORY1_INDEX = -4;
	public static final int ACCESSORY2_INDEX = -5;

	public static final int EQUIPMENT_SIZE = 4;

	public static boolean isEquipmentSlot(int index) {
		return index < -1;
	}

	private @Getter ItemStack heldItemStack;
	private @Getter ItemStack hand;
	private @Getter ItemStack clothing;
	private @Getter ItemStack accessory1;
	private @Getter ItemStack accessory2;

	public PlayerInventory(int size) {
		super(size);
	}

	public void setHeldItemStack(ItemStack itemStack) {
		if (!ItemStack.equals(itemStack, heldItemStack)) {
			ItemStack previous = heldItemStack;
			heldItemStack = itemStack;
			notifySlotChanged(HELD_ITEM_STACK_INDEX, previous, itemStack);
		}
	}

	public boolean setHand(ItemStack itemStack) {
		if (!ItemStack.equals(itemStack, hand) && itemStack.getItem() instanceof HandItem
				&& itemStack.getQuantity() == 1) {
			ItemStack previous = hand;
			hand = itemStack;
			notifySlotChanged(ITEM_IN_HAND_INDEX, previous, itemStack);
			return true;
		}
		return false;
	}

	public boolean setClothing(ItemStack itemStack) {
		if (!ItemStack.equals(itemStack, clothing) && itemStack.getItem() instanceof ClothingItem
				&& itemStack.getQuantity() == 1) {
			ItemStack previous = clothing;
			clothing = itemStack;
			notifySlotChanged(CLOTHING_INDEX, previous, itemStack);
			return true;
		}
		return false;
	}

	public boolean setAccessory1(ItemStack itemStack) {
		if (!ItemStack.equals(itemStack, accessory1) && itemStack.getItem() instanceof AccessoryItem
				&& itemStack.getQuantity() == 1) {
			ItemStack previous = accessory1;
			accessory1 = itemStack;
			notifySlotChanged(ACCESSORY1_INDEX, previous, itemStack);
			return true;
		}
		return false;
	}

	public boolean setAccessory2(ItemStack itemStack) {
		if (!ItemStack.equals(itemStack, accessory2) && itemStack.getItem() instanceof AccessoryItem
				&& itemStack.getQuantity() == 1) {
			ItemStack previous = accessory2;
			accessory2 = itemStack;
			notifySlotChanged(ACCESSORY2_INDEX, previous, itemStack);
			return true;
		}
		return false;
	}

	public void set(PlayerInventory other) {
		heldItemStack = other.heldItemStack;
		super.set(other);
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<PlayerInventory> {

		@Override
		public void write(Kryo kryo, Output output, PlayerInventory object) {
			output.writeShort(object.slots.length);
			for (ItemStack itemStack : object.slots) {
				kryo.writeObjectOrNull(output, itemStack, ItemStack.class);
			}
			kryo.writeObjectOrNull(output, object.heldItemStack, ItemStack.class);
			kryo.writeObjectOrNull(output, object.hand, ItemStack.class);
			kryo.writeObjectOrNull(output, object.clothing, ItemStack.class);
			kryo.writeObjectOrNull(output, object.accessory1, ItemStack.class);
			kryo.writeObjectOrNull(output, object.accessory2, ItemStack.class);
		}

		@Override
		public PlayerInventory read(Kryo kryo, Input input, Class<PlayerInventory> type) {
			short length = input.readShort();
			PlayerInventory inventory = new PlayerInventory(length);
			for (int i = 0; i < length; i++) {
				inventory.setSlot(i, kryo.readObjectOrNull(input, ItemStack.class));
			}
			inventory.setHeldItemStack(kryo.readObjectOrNull(input, ItemStack.class));
			inventory.setHand(kryo.readObjectOrNull(input, ItemStack.class));
			inventory.setClothing(kryo.readObjectOrNull(input, ItemStack.class));
			inventory.setAccessory1(kryo.readObjectOrNull(input, ItemStack.class));
			inventory.setAccessory2(kryo.readObjectOrNull(input, ItemStack.class));
			return inventory;
		}
	}
}
