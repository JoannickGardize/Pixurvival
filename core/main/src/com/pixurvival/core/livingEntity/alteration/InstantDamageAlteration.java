package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.entity.SourceProvider;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstantDamageAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	private float amount;

	@Override
	public void uniqueApply(SourceProvider source, LivingEntity entity) {
		entity.takeDamage(amount);
	}
}
