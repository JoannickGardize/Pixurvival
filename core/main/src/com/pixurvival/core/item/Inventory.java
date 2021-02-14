package com.pixurvival.core.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.item.Item;

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

	/**
	 * Set the given slot to the given itemStack.
	 * 
	 * @param index
	 * @param itemStack
	 */
	public void setSlot(int index, ItemStack itemStack) {
		if (!Objects.equals(itemStack, slots[index])) {
			ItemStack previousItemStack = slots[index];
			slots[index] = itemStack;
			slotChanged(index, previousItemStack, itemStack);
		}
	}

	public ItemStack getSlot(int index) {
		return slots[index];
	}

	public ItemStack take(int index) {
		ItemStack itemStack = getSlot(index);
		setSlot(index, null);
		return itemStack;
	}

	public boolean contains(Collection<ItemStack> itemStacks) {
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

	public boolean contains(Item item, int quantity) {
		return totalOf(item) >= quantity;
	}

	public int totalOf(Item item) {
		if (quantities == null || quantities.length <= item.getId()) {
			return 0;
		} else {
			return quantities[item.getId()];
		}
	}

	/**
	 * Try to take the given collection of item with the given quantity. If the
	 * quantity is not available nothing happen. The items are taken in priority
	 * from the end.
	 * 
	 * @param itemStacks
	 *            The collection of ItemStack to remove
	 * @return True if the ItemStacks were available and has been removed, false
	 *         overwise.
	 */
	public boolean remove(Collection<ItemStack> itemStacks) {
		if (!contains(itemStacks)) {
			return false;
		}

		for (ItemStack itemStack : itemStacks) {
			unsafeRemove(itemStack);
		}
		return true;
	}

	/**
	 * Try to take the given item with the given quantity. If the quantity is not
	 * available nothing happen. The items are taken in priority from the end.
	 * 
	 * @param itemStack
	 *            The ItemStack to remove
	 * @return True if the ItemStack were available and has been removed, false
	 *         overwise.
	 */
	public boolean remove(ItemStack itemStack) {
		if (!contains(itemStack)) {
			return false;
		}
		unsafeRemove(itemStack);
		return true;
	}

	/**
	 * Try to take the given item with the given quantity. If the quantity is not
	 * available nothing happen. The items are taken in priority from the end.
	 * 
	 * @param item
	 * @param quantity
	 * @return true if the quantity was successfully removed
	 */
	public boolean remove(Item item, int quantity) {
		if (!contains(item, quantity)) {
			return false;
		}
		unsafeRemove(item, quantity);
		return true;
	}

	public boolean unsafeRemove(ItemStack itemStack) {
		return unsafeRemove(itemStack.getItem(), itemStack.getQuantity());
	}

	/**
	 * Remove the maximum of quantity of the given item, in the limit of the
	 * maximumQuantity parameter. Used after a call to
	 * {@link Inventory#contains(ItemStack)}.
	 * 
	 * @param item
	 * @param maximumQuantity
	 * @return
	 */
	public boolean unsafeRemove(Item item, int maximumQuantity) {
		int remainingQuantity = maximumQuantity;
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

	public void removeAll() {
		for (int i = slots.length - 1; i >= 0; i--) {
			setSlot(i, null);
		}
	}

	/**
	 * Try to add the maximum quantity of the given ItemStack to this inventory, it
	 * will be stacked in priority with similar items if possible. It can be split
	 * into different slots if necessary.
	 * 
	 * @param itemStack
	 *            The ItemStack to add.
	 * @return An ItemStack containing the quantity that cannot be added to this
	 *         inventory, or null if all the quantity was added.
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
		int emptySlot = 0;
		while ((emptySlot = findEmptySlot(emptySlot)) != -1) {
			int maxQuantity = Math.min(remainingQuantity, itemStack.getItem().getMaxStackSize());
			setSlot(emptySlot, itemStack.copy(maxQuantity));
			remainingQuantity -= maxQuantity;
			if (remainingQuantity <= 0) {
				return null;
			}
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
		return findEmptySlot(0);
	}

	public int findEmptySlot(int startIndex) {
		for (int i = startIndex; i < slots.length; i++) {
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

	public void computeQuantities() {
		ensureQuantitiesArrayLength(0);
		Arrays.fill(quantities, 0);
		for (ItemStack itemStack : slots) {
			if (itemStack != null) {
				ensureQuantitiesArrayLength(itemStack.getItem().getId());
				quantities[itemStack.getItem().getId()] += itemStack.getQuantity();
			}
		}
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
			output.writeVarInt(object.slots.length, true);
			for (ItemStack itemStack : object.slots) {
				kryo.writeObjectOrNull(output, itemStack, ItemStack.class);
			}
		}

		@Override
		public Inventory read(Kryo kryo, Input input, Class<Inventory> type) {
			int length = input.readVarInt(true);
			Inventory inventory = new Inventory(length);
			for (int i = 0; i < length; i++) {
				inventory.slots[i] = kryo.readObjectOrNull(input, ItemStack.class);
			}
			return inventory;
		}
	}

}
