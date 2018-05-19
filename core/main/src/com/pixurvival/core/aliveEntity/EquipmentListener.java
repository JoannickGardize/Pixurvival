package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.item.ItemStack;

public interface EquipmentListener {

	void equipmentChanged(Equipment equipment, int equipmentIndex, ItemStack previousItemStack, ItemStack newItemStack);
}
