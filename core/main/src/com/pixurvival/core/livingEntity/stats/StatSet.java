package com.pixurvival.core.livingEntity.stats;

import java.nio.ByteBuffer;
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

	public void writeValues(ByteBuffer buffer) {
		buffer.putFloat(getValue(StatType.STRENGTH));
		buffer.putFloat(getValue(StatType.AGILITY));
		buffer.putFloat(getValue(StatType.INTELLIGENCE));
		buffer.putFloat(getValue(StatType.MAX_HEALTH));
		buffer.putFloat(getValue(StatType.ARMOR));
		buffer.putFloat(getValue(StatType.SPEED));
	}

	public void applyValues(ByteBuffer buffer) {
		get(StatType.STRENGTH).setValue(buffer.getFloat());
		get(StatType.AGILITY).setValue(buffer.getFloat());
		get(StatType.INTELLIGENCE).setValue(buffer.getFloat());
		get(StatType.MAX_HEALTH).setValue(buffer.getFloat());
		get(StatType.ARMOR).setValue(buffer.getFloat());
		get(StatType.SPEED).setValue(buffer.getFloat());
	}
}
