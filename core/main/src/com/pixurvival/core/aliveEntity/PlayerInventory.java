package com.pixurvival.core.aliveEntity;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;

public class PlayerInventory extends Inventory {

	public static final int HELD_ITEM_STACK_INDEX = -1;

	private @Getter ItemStack heldItemStack;

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
		}

		@Override
		public PlayerInventory read(Kryo kryo, Input input, Class<PlayerInventory> type) {
			short length = input.readShort();
			PlayerInventory inventory = new PlayerInventory(length);
			for (int i = 0; i < length; i++) {
				inventory.setSlot(i, kryo.readObjectOrNull(input, ItemStack.class));
			}
			inventory.heldItemStack = kryo.readObjectOrNull(input, ItemStack.class);
			return inventory;
		}
	}
}
