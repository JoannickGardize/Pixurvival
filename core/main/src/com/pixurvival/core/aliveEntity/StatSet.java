package com.pixurvival.core.aliveEntity;

import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.item.EquipableItem;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;

public class StatSet implements InventoryListener {

	private Map<StatType, StatValue> stats = new EnumMap<>(StatType.class);

	public StatSet() {
		for (StatType type : StatType.values()) {
			stats.put(type, new StatValue(this, type));
		}
		stats.values().forEach(v -> v.initialize());
	}

	public float valueOf(StatType type) {
		return stats.get(type).getValue();
	}

	public StatValue get(StatType type) {
		return stats.get(type);
	}

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		if (PlayerInventory.isEquipmentSlot(slotIndex)) {
			EquipableItem equipableItem = (EquipableItem) newItemStack.getItem();
			stats.get(StatType.STRENGTH).setEquipmentBonus(slotIndex, equipableItem.getStrengthBonus());
			stats.get(StatType.AGILITY).setEquipmentBonus(slotIndex, equipableItem.getStrengthBonus());
			stats.get(StatType.INTELLIGENCE).setEquipmentBonus(slotIndex, equipableItem.getStrengthBonus());
		}
	}
}
