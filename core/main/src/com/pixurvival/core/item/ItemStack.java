package com.pixurvival.core.item;

import java.io.Serializable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.message.WorldKryo;

import lombok.Data;

@Data
public class ItemStack implements Serializable {

	private static final long serialVersionUID = 1L;

	@ElementReference
	private Item item;

	@Bounds(min = 1)
	private int quantity;

	public ItemStack() {
		this(null, 1);
	}

	public ItemStack(Item item) {
		this(item, 1);
	}

	public ItemStack(Item item, int quantity) {
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

	public ItemStack copy() {
		return new ItemStack(item, quantity);
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

	/**
	 * Substract one from this ItemStack and returns the result as a new ItemStack.
	 * if the quantity become zero, null is returned.
	 * 
	 * @param quantity
	 * @return
	 */
	public ItemStack sub(int quantity) {
		int newQuantity = this.quantity - quantity;
		if (newQuantity > 0) {
			return new ItemStack(item, newQuantity);
		} else if (newQuantity == 0) {
			return null;
		} else {
			throw new IllegalStateException("Not enough quantity in the ItemStack");
		}
	}

	public ItemStack add(int quantity) {
		return new ItemStack(item, this.quantity + quantity);
	}

	@Override
	public String toString() {
		return quantity + " " + item.getName();
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
			ContentPack pack = ((WorldKryo) kryo).getWorld().getContentPack();
			if (pack == null) {
				return null;
			}
			return new ItemStack(pack.getItems().get(itemId), quantity);
		}

	}
}
