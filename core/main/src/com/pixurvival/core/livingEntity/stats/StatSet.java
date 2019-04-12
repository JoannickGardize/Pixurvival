package com.pixurvival.core.livingEntity.stats;

import java.util.EnumMap;
import java.util.Map;

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

	public void addModifier(StatModifier modifier) {
		stats.get(modifier.getStatType()).addModifier(modifier);
	}

	public void removeModifier(StatModifier modifier) {
		stats.get(modifier.getStatType()).removeModifier(modifier);
	}
}
