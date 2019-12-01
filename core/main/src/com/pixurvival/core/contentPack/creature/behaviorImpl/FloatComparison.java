package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.util.FloatBiPredicate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FloatComparison implements FloatBiPredicate {

	GREATER_THAN((d1, d2) -> d1 > d2),
	LESS_THAN((d1, d2) -> d1 < d2);

	private FloatBiPredicate test;

	@Override
	public boolean test(float d1, float d2) {
		return test.test(d1, d2);
	}

}
