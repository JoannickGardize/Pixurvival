package com.pixurvival.core.livingEntity.stats;

import java.util.ArrayList;
import java.util.Collection;

import com.pixurvival.core.util.MathUtils;

import lombok.Getter;

@Getter
public class StatValue implements StatListener {

	private StatSet statSet;
	private StatType type;
	private float value;

	private float base;

	private Collection<StatListener> listeners = new ArrayList<>();
	private Collection<StatModifier> modifiers = new ArrayList<>();

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
	 * Directly set the value, by-passing the regular computed value
	 * 
	 * @param value
	 */
	public void setValue(float value) {
		if (this.value != value) {
			float oldValue = this.value;
			this.value = value;
			listeners.forEach(l -> l.statChanged(oldValue, this));
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
	public void statChanged(float oldValue, StatValue statValue) {
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
			float oldValue = value;
			value = newValue;
			listeners.forEach(l -> l.statChanged(oldValue, this));
		}
	}
}
