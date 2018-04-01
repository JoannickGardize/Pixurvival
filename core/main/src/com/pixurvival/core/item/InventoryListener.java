package com.pixurvival.core.item;

public interface InventoryListener {

	void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack);

}
