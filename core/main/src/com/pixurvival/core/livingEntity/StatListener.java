package com.pixurvival.core.livingEntity;

public interface StatListener {

	void changed(StatValue statValue);

	default void baseChanged(StatValue statValue) {

	}
}
