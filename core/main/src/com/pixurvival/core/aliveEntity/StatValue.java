package com.pixurvival.core.aliveEntity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class StatValue implements StatListener {

	private StatSet statSet;
	private StatType type;
	private float value;

	private float base;

	private List<StatListener> listeners = new ArrayList<>();
	private float[] equipmentBonuses = new float[PlayerInventory.EQUIPMENT_SIZE];

	public void addListener(StatListener listener) {
		listeners.add(listener);
	}

	public StatValue(StatSet set, StatType type) {
		this.statSet = set;
		this.type = type;
	}

	public void initialize() {
		for (StatType dependency : type.getDependencies()) {
			statSet.get(dependency).addListener(this);
		}
		compute();
	}

	public void setBase(float base) {
		if (this.base != base) {
			this.base = base;
			compute();
		}
	}

	public void setValue(float value) {
		if (this.value != value) {
			this.value = value;
		}
	}

	public void setEquipmentBonus(int slotIndex, float value) {
		int index = PlayerInventory.EQUIPMENT_SIZE + 1 + slotIndex;
		if (equipmentBonuses[index] != value) {
			equipmentBonuses[index] = value;
			compute();
		}
	}

	@Override
	public void changed(StatValue statValue) {
		compute();
	}

	private void compute() {
		float newValue = base + type.getFormula().apply(statSet);
		for (int i = 0; i < equipmentBonuses.length; i++) {
			newValue += equipmentBonuses[i];
		}
		if (newValue != value) {
			value = newValue;
			listeners.forEach(l -> l.changed(this));
		}
	}

}
