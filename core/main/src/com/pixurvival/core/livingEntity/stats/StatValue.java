package com.pixurvival.core.livingEntity.stats;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.util.MathUtils;

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

	/**
	 * Directly set the value, used by the client side
	 * 
	 * @param value
	 */
	public void setValue(float value) {
		if (this.value != value) {
			this.value = value;
			listeners.forEach(l -> l.statChanged(this));
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
		float newValue = MathUtils.clamp((base + type.getFormula().compute(statSet) + absoluteModifier) * relativeModifier, type.getMinimum(), type.getMaximum());
		if (newValue != value) {
			value = newValue;
			listeners.forEach(l -> l.statChanged(this));
		}
	}
}
