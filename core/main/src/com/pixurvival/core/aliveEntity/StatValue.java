package com.pixurvival.core.aliveEntity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class StatValue implements StatListener {

	private StatSet statSet;
	private StatType type;
	private float value;

	private List<StatListener> listeners = new ArrayList<>();

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

	public void setValue(float value) {
		if (this.value != value) {
			this.value = value;
			listeners.forEach(l -> l.changed(this));
		}
	}

	@Override
	public void changed(StatValue statValue) {
		compute();
	}

	private void compute() {
		value = type.getFormula().apply(statSet);
	}

}
