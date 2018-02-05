package fr.sharkhendrix.pixurvival.core;

public interface Damageable {

	double getHealth();

	double getMaxHealth();

	void takeDamage(double amount);

	void takeHeal(double amount);
}
