package com.pixurvival.core;

import com.pixurvival.core.alteration.DamageAttributes;

public interface Damageable {

	float getHealth();

	float getMaxHealth();

	void takeDamage(float amount, DamageAttributes attributes);

	default float getPercentHealth() {
		return getHealth() / getMaxHealth();
	}
}
