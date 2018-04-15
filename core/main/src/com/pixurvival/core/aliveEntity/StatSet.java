package com.pixurvival.core.aliveEntity;

import java.util.EnumMap;
import java.util.Map;

public class StatSet {

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

	public void set(StatType type, float value) {
		stats.get(type).setValue(value);
	}

	public StatValue get(StatType type) {
		return stats.get(type);
	}
}
