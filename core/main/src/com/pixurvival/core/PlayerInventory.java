package com.pixurvival.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class PlayerInventory extends Inventory {

	private @Getter @Setter ItemStack heldItemStack;

	public PlayerInventory(int size) {
		super(size);
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
