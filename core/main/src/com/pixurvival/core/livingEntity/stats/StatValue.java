package com.pixurvival.core.livingEntity.stats;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.livingEntity.alteration.PersistentStatAlteration;
import com.pixurvival.core.livingEntity.alteration.StatAlterationOperation;

import lombok.Getter;

@Getter
public class StatValue implements StatListener {

	private StatSet statSet;
	private StatType type;
	private float value;

	private float base;

	private List<StatListener> listeners = new ArrayList<>();
	private float[] equipmentBonuses = new float[Equipment.EQUIPMENT_SIZE];
	private List<PersistentStatAlteration> alterations = new ArrayList<>();

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
			listeners.forEach(l -> l.baseChanged(this));
			compute();
		}
	}

	public void addAlteration(PersistentStatAlteration alteration) {
		alterations.add(alteration);
		compute();
	}

	public void removeAlteration(PersistentStatAlteration alteration) {
		if (alterations.remove(alteration)) {
			compute();
		}
	}

	@Override
	public void changed(StatValue statValue) {
		compute();
	}

	public void compute() {
		float absoluteModifier = 0;
		float relativeModifier = 1;
		for (PersistentStatAlteration alteration : alterations) {
			if (alteration.getOperation() == StatAlterationOperation.ADDITIVE) {
				absoluteModifier += alteration.getValue();
			} else {
				relativeModifier += alteration.getValue();
			}
		}
		float newValue = (base + type.getFormula().apply(statSet) + absoluteModifier) * relativeModifier;
		if (newValue != value) {
			value = newValue;
			listeners.forEach(l -> l.changed(this));
		}
	}

}
