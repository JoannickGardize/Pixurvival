package com.pixurvival.core.item;

import java.util.Collection;
import java.util.function.Consumer;

import com.pixurvival.core.contentPack.item.Item;

import lombok.Getter;

/**
 * Empty inventory implementation to boosts performances
 * 
 * @author SharkHendrix
 *
 */
public class EmptyInventoryProxy extends Inventory {

	private static final @Getter EmptyInventoryProxy instance = new EmptyInventoryProxy();

	private EmptyInventoryProxy() {
		super(0);
	}

	@Override
	public void set(Inventory other) {
		// Nothing to set
	}

	@Override
	public void addListener(InventoryListener listener) {
		// Nothing to listen
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean contains(Collection<ItemStack> itemStacks) {
		return false;
	}

	@Override
	public boolean contains(ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean contains(Item item, int quantity) {
		return false;
	}

	@Override
	public int totalOf(Item item) {
		return 0;
	}

	@Override
	public boolean remove(Collection<ItemStack> itemStacks) {
		return false;
	}

	@Override
	public boolean remove(ItemStack itemStack) {
		return false;
	}

	@Override
	public boolean remove(Item item, int quantity) {
		return false;
	}

	@Override
	public ItemStack add(ItemStack itemStack) {
		return itemStack.copy();
	}

	@Override
	public void foreachItemStacks(Consumer<ItemStack> action) {
	}

	@Override
	public int findEmptySlot() {
		return -1;
	}

	@Override
	public int findEmptySlot(int startIndex) {
		return -1;
	}

	@Override
	public void slotChanged(int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
	}

	@Override
	public void computeQuantities() {
	}

	@Override
	protected void notifySlotChanged(int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
	}
}
