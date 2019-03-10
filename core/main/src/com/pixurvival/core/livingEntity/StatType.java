package com.pixurvival.core.livingEntity;

import java.util.function.Function;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum StatType {

	STRENGTH,
	AGILITY,
	INTELLIGENCE,
	MAX_HEALTH(s -> 100 + s.getValue(STRENGTH) * 10, STRENGTH),
	SPEED(s -> 8 + s.getValue(AGILITY) * 0.5f, AGILITY),
	ARMOR(s -> s.getValue(STRENGTH), STRENGTH);

	private Function<StatSet, Float> formula = s -> 0f;
	private StatType[] dependencies = new StatType[0];

	private StatType(Function<StatSet, Float> formula, StatType... dependencies) {
		this.formula = formula;
		this.dependencies = dependencies;
	}

	public StatType[] primaryStats() {
		return new StatType[] { STRENGTH, AGILITY, INTELLIGENCE };
	}
}
