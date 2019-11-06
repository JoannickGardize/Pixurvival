package com.pixurvival.core.livingEntity.stats;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum StatType {

	STRENGTH,
	AGILITY,
	INTELLIGENCE,
	MAX_HEALTH(s -> 100 + s.getValue(STRENGTH) * 10, 1, Float.POSITIVE_INFINITY, STRENGTH),
	SPEED(s -> 5 + s.getValue(AGILITY) * 0.2f, 1, Float.POSITIVE_INFINITY, AGILITY),
	ARMOR(s -> s.getValue(STRENGTH) > 0 ? s.getValue(STRENGTH) / (s.getValue(STRENGTH) + 30) : 0, 0, 0.9f, STRENGTH);

	public interface Formula {
		public float compute(StatSet statSet);
	}

	private Formula formula = s -> 0f;
	private StatType[] dependencies = new StatType[0];
	private float minimum = Float.NEGATIVE_INFINITY;
	private float maximum = Float.POSITIVE_INFINITY;

	private StatType(Formula formula, float minimum, float maximum, StatType... dependencies) {
		this.minimum = minimum;
		this.maximum = maximum;
		this.formula = formula;
		this.dependencies = dependencies;
	}
}
