package com.pixurvival.core.livingEntity;

import com.pixurvival.core.item.ItemStack;

public interface EquipmentListener {

	void equipmentChanged(Equipment equipment, int equipmentIndex, ItemStack previousItemStack, ItemStack newItemStack);
}
