package com.pixurvival.core.aliveEntity;

public interface StatListener {

	void changed(StatValue statValue);

	default void baseChanged(StatValue statValue) {

	}
}
