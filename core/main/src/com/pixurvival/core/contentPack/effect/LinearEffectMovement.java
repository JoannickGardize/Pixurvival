package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinearEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	private double speed;

	@Override
	public void initialize(EffectEntity entity) {
		LivingEntity source = entity.getSource();
		entity.getPosition().set(source.getPosition());
		entity.setMovingAngle(source.getPosition().angleToward(source.getTargetPosition()));
		entity.setForward(true);
	}

	@Override
	public void update(EffectEntity entity) {
		// Nothing
	}

	@Override
	public double getSpeedPotential() {
		return speed;
	}
}
