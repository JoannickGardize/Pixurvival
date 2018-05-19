package com.pixurvival.core.aliveEntity;

import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.item.EquipableItem;
import com.pixurvival.core.item.ItemStack;

public class StatSet implements EquipmentListener {

	private Map<StatType, StatValue> stats = new EnumMap<>(StatType.class);

	public StatSet() {
		for (StatType type : StatType.values()) {
			stats.put(type, new StatValue(this, type));
		}
		stats.values().forEach(v -> v.initialize());
	}

	public void addListener(StatListener listener) {
		stats.values().forEach(v -> v.addListener(listener));
	}

	public float valueOf(StatType type) {
		return stats.get(type).getValue();
	}

	public StatValue get(StatType type) {
		return stats.get(type);
	}

	@Override
	public void equipmentChanged(Equipment equipment, int equipmentIndex, ItemStack previousItemStack,
			ItemStack newItemStack) {
		if (newItemStack != null && newItemStack.getItem() instanceof EquipableItem) {
			EquipableItem equipableItem = (EquipableItem) newItemStack.getItem();
			stats.get(StatType.STRENGTH).setEquipmentBonus(equipmentIndex, equipableItem.getStrengthBonus());
			stats.get(StatType.AGILITY).setEquipmentBonus(equipmentIndex, equipableItem.getStrengthBonus());
			stats.get(StatType.INTELLIGENCE).setEquipmentBonus(equipmentIndex, equipableItem.getStrengthBonus());
		}
	}
}
