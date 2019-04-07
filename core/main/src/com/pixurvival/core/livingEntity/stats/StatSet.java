package com.pixurvival.core.livingEntity.stats;

import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.livingEntity.alteration.PersistentStatAlteration;

public class StatSet {

	private Map<StatType, StatValue> stats = new EnumMap<>(StatType.class);

	public StatSet() {
		for (StatType type : StatType.values()) {
			stats.put(type, new StatValue(this, type));
		}
		stats.values().forEach(StatValue::initialize);
	}

	public void computeAll() {
		stats.values().forEach(StatValue::compute);
	}

	public void addListener(StatListener listener) {
		stats.values().forEach(v -> v.addListener(listener));
	}

	public float getValue(StatType type) {
		return stats.get(type).getValue();
	}

	public StatValue get(StatType type) {
		return stats.get(type);
	}

	public void addAlteration(PersistentStatAlteration alteration) {
		stats.get(alteration.getStatType()).addAlteration(alteration);
	}

	public void removeAlteration(PersistentStatAlteration alteration) {
		stats.get(alteration.getStatType()).removeAlteration(alteration);
	}
}
