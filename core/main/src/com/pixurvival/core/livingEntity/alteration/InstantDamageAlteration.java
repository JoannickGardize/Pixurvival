package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;

public class InstantDamageAlteration implements Alteration {

	private float amount;

	@Override
	public void apply(LivingEntity entity) {
		entity.takeDamage(amount);
	}
}
