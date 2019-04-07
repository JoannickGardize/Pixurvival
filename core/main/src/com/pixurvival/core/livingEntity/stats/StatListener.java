package com.pixurvival.core.livingEntity.stats;

public interface StatListener {

	void changed(StatValue statValue);

	default void baseChanged(StatValue statValue) {

	}
}
