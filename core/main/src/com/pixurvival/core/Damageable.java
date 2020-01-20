package com.pixurvival.core;

public interface Damageable {

	float getHealth();

	float getMaxHealth();

	void takeDamage(float amount);

	default float getPercentHealth() {
		return getHealth() / getMaxHealth();
	}
}
