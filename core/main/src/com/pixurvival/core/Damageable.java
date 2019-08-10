package com.pixurvival.core;

public interface Damageable {

	float getHealth();

	float getMaxHealth();

	void takeDamage(float amount);

	void takeHeal(float amount);

	default double getPercentHealth() {
		return getHealth() / getMaxHealth();
	}
}
