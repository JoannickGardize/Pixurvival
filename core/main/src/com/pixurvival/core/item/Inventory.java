package com.pixurvival.core.item;

import java.util.function.Consumer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;

public class Inventory {

	private ItemStack[] slots;

	public Inventory(int size) {
		slots = new ItemStack[size];
	}

	public int getSize() {
		return slots.length;
	}

	public void setSlot(int index, ItemStack itemStack) {
		slots[index] = itemStack;
	}

	public ItemStack getSlot(int index) {
		return slots[index];
	}

	public ItemStack take(int index) {
		ItemStack removed = slots[index];
		slots[index] = null;
		return removed;
	}

	public boolean contains(Item item, int quantity) {
		int remainingQuantity = quantity;
		for (int i = 0; i < slots.length; i++) {
			ItemStack slot = slots[i];
			if (slot != null && slot.getItem() == item) {
				if (slot.getQuantity() >= remainingQuantity) {
					return true;
				} else {
					remainingQuantity -= slot.getQuantity();
				}
			}
		}
		return false;
	}

	/**
	 * Try to take the given item with the given quantity. If the quantity is
	 * not available nothing happen. The items are taken in priority from the
	 * end.
	 * 
	 * @param item
	 *            The item to take.
	 * @param quantity
	 *            the quantity of the item to take.
	 * @return The ItemStack containing the item and the quantity required, or
	 *         null of not available
	 */
	public ItemStack take(Item item, int quantity) {
		if (!contains(item, quantity)) {
			return null;
		}
		int remainingQuantity = quantity;
		for (int i = slots.length - 1; i >= 0; i--) {
			ItemStack slot = slots[i];
			if (slot != null && slot.getItem() == item) {
				int removed = slot.removeQuantity(remainingQuantity);
				remainingQuantity -= removed;
				if (slot.getQuantity() == 0) {
					slots[i] = null;
				}
				if (remainingQuantity == 0) {
					return new ItemStack(item, quantity);
				}
			}
		}
		Log.warn("Something that should never happen happened !");
		return null;
	}

	/**
	 * Try to add the maximum quantity of the given ItemStack to this inventory,
	 * it will be stacked with similar items if possible. It can be spited into
	 * different slots if necessary.
	 * 
	 * @param itemStack
	 *            The ItemStack to add. If the ItemStack cannot be fully added,
	 *            the quantity of the itemStack is updated.
	 * @return True if all the ItemStack is added, false otherwise.
	 */
	public boolean smartAdd(ItemStack itemStack) {
		Item item = itemStack.getItem();
		for (int i = 0; i < slots.length; i++) {
			ItemStack slot = slots[i];
			if (slot != null && slot.getItem() == item) {
				itemStack.setQuantity(slot.addQuantity(itemStack.getQuantity()));
				if (itemStack.getQuantity() == 0) {
					return true;
				}
			}
		}
		int emptySlot = findEmptySlot();
		if (emptySlot != -1) {
			slots[emptySlot] = new ItemStack(itemStack);
			itemStack.setQuantity(0);
			return true;
		}
		return false;
	}

	public void foreachItemStacks(Consumer<ItemStack> action) {
		for (int i = 0; i < slots.length; i++) {
			ItemStack slot = slots[i];
			if (slot != null) {
				action.accept(slot);
			}
		}
	}

	public int findEmptySlot() {
		for (int i = 0; i < slots.length; i++) {
			if (slots[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<Inventory> {

		@Override
		public void write(Kryo kryo, Output output, Inventory object) {
			output.writeShort(object.slots.length);
			for (ItemStack itemStack : object.slots) {
				if (itemStack == null) {
					kryo.writeObjectOrNull(output, object, ItemStack.class);
				}
			}
		}

		@Override
		public Inventory read(Kryo kryo, Input input, Class<Inventory> type) {
			short length = input.readShort();
			Inventory inventory = new Inventory(length);
			for (int i = 0; i < length; i++) {
				inventory.setSlot(i, kryo.readObjectOrNull(input, ItemStack.class));
			}
			return inventory;
		}

	}
}
