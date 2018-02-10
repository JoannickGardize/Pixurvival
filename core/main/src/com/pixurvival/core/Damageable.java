package com.pixurvival.core;

public interface Damageable {

	double getHealth();

	double getMaxHealth();

	void takeDamage(double amount);

	void takeHeal(double amount);
}
