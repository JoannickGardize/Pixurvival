package com.pixurvival.core.livingEntity.stats;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.livingEntity.Equipment;

import lombok.Getter;

@Getter
public class StatValue implements StatListener {

	private StatSet statSet;
	private StatType type;
	private float value;

	private float base;

	private List<StatListener> listeners = new ArrayList<>();
	private float[] equipmentBonuses = new float[Equipment.EQUIPMENT_SIZE];
	private List<StatModifier> modifiers = new ArrayList<>();

	public void addListener(StatListener listener) {
		listeners.add(listener);
	}

	public StatValue(StatSet set, StatType type) {
		this.statSet = set;
		this.type = type;
	}

	void initialize() {
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

	public void addToBase(float toAdd) {
		if (toAdd != 0) {
			this.base += toAdd;
			compute();
		}
	}

	public void addModifier(StatModifier modifier) {
		modifiers.add(modifier);
		compute();
	}

	public void removeModifier(StatModifier modifier) {
		if (modifiers.remove(modifier)) {
			compute();
		}
	}

	@Override
	public void statChanged(StatValue statValue) {
		compute();
	}

	public void compute() {
		float absoluteModifier = 0;
		float relativeModifier = 1;
		for (StatModifier modifier : modifiers) {
			if (modifier.getOperationType() == StatModifier.OperationType.ADDITIVE) {
				absoluteModifier += modifier.getValue();
			} else {
				relativeModifier += modifier.getValue();
			}
		}
		float newValue = (base + type.getFormula().apply(statSet) + absoluteModifier) * relativeModifier;
		if (newValue != value) {
			value = newValue;
			listeners.forEach(l -> l.statChanged(this));
		}
	}
}
