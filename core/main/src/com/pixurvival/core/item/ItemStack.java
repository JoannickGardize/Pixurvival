package com.pixurvival.core.item;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ContentPack;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class ItemStack {

	private Item item;
	private @Setter int quantity;

	public ItemStack(Item item) {
		this.item = item;
		quantity = 1;
	}

	public ItemStack(Item item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public ItemStack(ItemStack other) {
		item = other.item;
		quantity = other.quantity;
	}

	public static boolean equals(ItemStack itemStack1, ItemStack itemStack2) {
		if (itemStack1 != null) {
			return itemStack1.equals(itemStack2);
		}
		return itemStack2 == null;
	}

	/**
	 * try to add the given quantity to this itemStack.
	 * 
	 * @param quantity
	 *            the quantity to add.
	 * @return The quantity that cannot be added, according to
	 *         {@link Item#getMaxStackSize()}.
	 */
	public int addQuantity(int quantity) {
		this.quantity += quantity;
		if (this.quantity > item.getMaxStackSize()) {
			int rest = this.quantity - item.getMaxStackSize();
			this.quantity = item.getMaxStackSize();
			return rest;
		}
		return 0;
	}

	/**
	 * try to remove the given quantity.
	 * 
	 * @param quantity
	 *            The quantity to remove.
	 * @return The quantity effectively removed.
	 */
	public int removeQuantity(int quantity) {
		if (this.quantity >= quantity) {
			this.quantity -= quantity;
			return quantity;
		} else {
			int removed = this.quantity;
			this.quantity = 0;
			return removed;
		}
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
