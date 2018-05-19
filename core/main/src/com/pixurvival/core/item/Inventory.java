package com.pixurvival.core.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Inventory {

	protected ItemStack[] slots;
	private int[] quantities;
	private List<InventoryListener> listeners = new ArrayList<>();

	public Inventory(int size) {
		slots = new ItemStack[size];
	}

	public void set(Inventory other) {
		for (int i = 0; i < slots.length; i++) {
			setSlot(i, other.getSlot(i));
		}
	}

	public void addListener(InventoryListener listener) {
		listeners.add(listener);
	}

	public int size() {
		return slots.length;
	}

	public ItemStack setSlot(int index, ItemStack itemStack) {
		if (!ItemStack.equals(itemStack, slots[index])) {
			ItemStack previousItemStack = slots[index];
			slots[index] = itemStack;
			slotChanged(index, previousItemStack, itemStack);
			return previousItemStack;
		}
		return itemStack;
	}

	public ItemStack getSlot(int index) {
		return slots[index];
	}

	public ItemStack take(int index) {
		ItemStack itemStack = getSlot(index);
		setSlot(index, null);
		return itemStack;
	}

	public boolean contains(ItemStack... itemStacks) {
		for (ItemStack itemStack : itemStacks) {
			if (totalOf(itemStack.getItem()) < itemStack.getQuantity()) {
				return false;
			}
		}
		return true;
	}

	public boolean contains(ItemStack itemStack) {
		return totalOf(itemStack.getItem()) >= itemStack.getQuantity();
	}

	public int totalOf(Item item) {
		if (quantities == null || quantities.length <= item.getId()) {
			return 0;
		} else {
			return quantities[item.getId()];
		}
	}

	/**
	 * Try to take the given item with the given quantity. If the quantity is not
	 * available nothing happen. The items are taken in priority from the end.
	 * 
	 * @param item
	 *            The item to take.
	 * @param quantity
	 *            the quantity of the item to take.
	 * @return The ItemStack taken, or null if not available.
	 */
	public boolean remove(ItemStack... itemStacks) {
		if (!contains(itemStacks)) {
			return false;
		}

		for (ItemStack itemStack : itemStacks) {
			unsafeRemove(itemStack);
		}
		return true;
	}

	public boolean unsafeRemove(ItemStack itemStack) {
		return unsafeRemove(itemStack.getItem(), itemStack.getQuantity());
	}

	public boolean unsafeRemove(Item item, int quantity) {
		int remainingQuantity = quantity;
		for (int i = slots.length - 1; i >= 0; i--) {
			ItemStack slot = slots[i];
			if (slot != null && slot.getItem() == item) {
				if (slot.getQuantity() > remainingQuantity) {
					setSlot(i, slot.sub(remainingQuantity));
					return true;
				} else {
					setSlot(i, null);
					if (slot.getQuantity() == remainingQuantity) {
						return true;
					} else {
						remainingQuantity -= slot.getQuantity();
					}
				}
			}
		}
		return false;
	}

	/**
	 * Try to add the maximum quantity of the given ItemStack to this inventory, it
	 * will be stacked with similar items if possible. It can be spited into
	 * different slots if necessary.
	 * 
	 * @param itemStack
	 *            The ItemStack to add. If the ItemStack cannot be fully added, the
	 *            quantity of the itemStack is updated.
	 * @return True if all the ItemStack is added, false otherwise.
	 */
	public ItemStack add(ItemStack itemStack) {
		Item item = itemStack.getItem();
		int remainingQuantity = itemStack.getQuantity();
		for (int i = 0; i < slots.length; i++) {
			ItemStack slot = slots[i];
			if (slot != null && slot.getItem() == item) {
				int overflow = slot.overflowingQuantity(remainingQuantity);
				setSlot(i, slot.add(remainingQuantity - overflow));
				if (overflow == 0) {
					return null;
				}
				remainingQuantity = overflow;
			}
		}
		int emptySlot = findEmptySlot();
		if (emptySlot != -1) {
			setSlot(emptySlot, itemStack.copy(remainingQuantity));
			return null;
		}
		return itemStack.copy(remainingQuantity);
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

	public boolean isValidIndex(int index) {
		return index >= 0 && index < slots.length;
	}

	public void slotChanged(int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {

		if (previousItemStack == null) {
			ensureQuantitiesArrayLength(newItemStack.getItem().getId());
			quantities[newItemStack.getItem().getId()] += newItemStack.getQuantity();
		} else if (newItemStack == null) {
			ensureQuantitiesArrayLength(previousItemStack.getItem().getId());
			quantities[previousItemStack.getItem().getId()] -= previousItemStack.getQuantity();
		} else if (previousItemStack.getItem() == newItemStack.getItem()) {
			ensureQuantitiesArrayLength(previousItemStack.getItem().getId());
			quantities[newItemStack.getItem().getId()] += newItemStack.getQuantity() - previousItemStack.getQuantity();
		} else {
			ensureQuantitiesArrayLength(Math.max(newItemStack.getItem().getId(), previousItemStack.getItem().getId()));
			quantities[previousItemStack.getItem().getId()] -= previousItemStack.getQuantity();
			quantities[newItemStack.getItem().getId()] += newItemStack.getQuantity();
		}
		notifySlotChanged(slotIndex, previousItemStack, newItemStack);
	}

	protected void notifySlotChanged(int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		listeners.forEach(l -> l.slotChanged(this, slotIndex, previousItemStack, newItemStack));
	}

	private void ensureQuantitiesArrayLength(int maxIndex) {
		if (quantities == null) {
			quantities = new int[maxIndex + 5];
		} else if (quantities.length <= maxIndex) {
			int[] newArray = new int[maxIndex + 5];
			System.arraycopy(quantities, 0, newArray, 0, quantities.length);
			quantities = newArray;
		}
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<Inventory> {

		@Override
		public void write(Kryo kryo, Output output, Inventory object) {
			output.writeShort(object.slots.length);
			for (ItemStack itemStack : object.slots) {
				kryo.writeObjectOrNull(output, itemStack, ItemStack.class);
			}
		}

		@Override
		public Inventory read(Kryo kryo, Input input, Class<Inventory> type) {
			short length = input.readShort();
			Inventory inventory = new Inventory(length);
			for (int i = 0; i < length; i++) {
				inventory.slots[i] = kryo.readObjectOrNull(input, ItemStack.class);
			}
			return inventory;
		}
	}

}
