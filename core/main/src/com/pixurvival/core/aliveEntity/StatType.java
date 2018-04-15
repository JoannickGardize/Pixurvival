package com.pixurvival.core.aliveEntity;

import java.util.function.Function;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum StatType {

	STRENGTH,
	AGILITY,
	INTELLIGENCE,
	MAX_HEALTH(s -> 100 + s.valueOf(STRENGTH) * 10, STRENGTH),
	SPEED(s -> 8 + s.valueOf(AGILITY) * 0.5f, AGILITY),
	ARMOR(s -> s.valueOf(STRENGTH), STRENGTH);

	private Function<StatSet, Float> formula = s -> 0f;
	private StatType[] dependencies = new StatType[0];

	private StatType(Function<StatSet, Float> formula, StatType... dependencies) {
		this.formula = formula;
		this.dependencies = dependencies;
	}

}
