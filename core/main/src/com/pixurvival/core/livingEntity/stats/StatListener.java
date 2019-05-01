package com.pixurvival.core.livingEntity.stats;

public interface StatListener {

	void statChanged(StatValue statValue);

	default void baseStatChanged(StatValue statValue) {

	}
}
