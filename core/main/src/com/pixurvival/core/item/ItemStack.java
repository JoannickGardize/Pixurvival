package com.pixurvival.core.item;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;

import lombok.NonNull;
import lombok.Value;

@Value
public class ItemStack {

	private @NonNull Item item;
	private int quantity;

	public ItemStack(Item item) {
		this(item, 1);
	}

	public ItemStack(Item item, int quantity) {
		if (item == null || quantity < 1) {
			throw new IllegalArgumentException();
		}
		this.item = item;
		this.quantity = quantity;
	}

	public ItemStack(ItemStack other) {
		item = other.item;
		quantity = other.quantity;
	}

	public ItemStack copy(int quantity) {
		return new ItemStack(item, quantity);
	}

	public static boolean equals(ItemStack itemStack1, ItemStack itemStack2) {
		if (itemStack1 != null) {
			return itemStack1.equals(itemStack2);
		}
		return itemStack2 == null;
	}

	/**
	 * Return the overflowing quantity if added to the quantity of this item stack,
	 * according to {@link Item#getMaxStackSize()}.
	 * 
	 * @param quantity
	 *            the quantity to add.
	 * @return The overflowing quantity.
	 */
	public int overflowingQuantity(int quantity) {
		int result = this.quantity + quantity - item.getMaxStackSize();
		if (result < 0) {
			return 0;
		} else {
			return result;
		}
	}

	public ItemStack sub(int quantity) {
		return new ItemStack(item, this.quantity - quantity);
	}

	public ItemStack add(int quantity) {
		return new ItemStack(item, this.quantity + quantity);
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<ItemStack> {

		@Override
		public void write(Kryo kryo, Output output, ItemStack object) {
			output.writeShort(object.getItem().getId());
			output.writeShort(object.getQuantity());
		}

		@Override
		public ItemStack read(Kryo kryo, Input input, Class<ItemStack> type) {
			short itemId = input.readShort();
			short quantity = input.readShort();
			ContentPack pack = World.getCurrentContentPack();
			if (pack == null) {
				return null;
			}
			return new ItemStack(pack.getItemsById().get(itemId), quantity);
		}

	}
}
