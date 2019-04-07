package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;

public class InstantHealAlteration implements Alteration {

	private float amount;

	@Override
	public void apply(LivingEntity entity) {
		entity.takeHeal(amount);
	}

}
