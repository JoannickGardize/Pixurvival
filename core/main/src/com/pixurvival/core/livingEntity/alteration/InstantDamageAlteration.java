package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;

public class InstantDamageAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	private float amount;

	@Override
	public void apply(Object source, LivingEntity entity) {
		entity.takeDamage(amount);
	}
}
