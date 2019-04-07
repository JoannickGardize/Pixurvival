package com.pixurvival.core.livingEntity.stats;

import java.util.function.Function;

import com.pixurvival.core.util.MathUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum StatType {

	STRENGTH,
	AGILITY,
	INTELLIGENCE,
	MAX_HEALTH(s -> Math.max(100 + s.getValue(STRENGTH) * 10, 1), STRENGTH),
	SPEED(s -> Math.max(8 + s.getValue(AGILITY) * 0.5f, 0), AGILITY),
	ARMOR(s -> s.getValue(STRENGTH) > 0 ? MathUtils.clamp(s.getValue(STRENGTH) / (s.getValue(STRENGTH) + 30), 0, 0.9f) : 0, STRENGTH);

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
