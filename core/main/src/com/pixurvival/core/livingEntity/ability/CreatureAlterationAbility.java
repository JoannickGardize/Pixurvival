package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatureAlterationAbility extends AlterationAbility {

	private static final long serialVersionUID = 1L;

	private double predictionBulletSpeed;

	@Override
	public boolean fire(LivingEntity entity) {
		return super.fire(entity);
	}
}
