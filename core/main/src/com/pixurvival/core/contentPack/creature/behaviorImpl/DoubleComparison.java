package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.util.DoubleBiPredicate;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DoubleComparison implements DoubleBiPredicate {

	GREATER_THAN((d1, d2) -> d1 > d2),
	LESS_THAN((d1, d2) -> d1 < d2);

	private DoubleBiPredicate test;

	@Override
	public boolean test(double d1, double d2) {
		return test.test(d1, d2);
	}

}
