package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

public class AnchorEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private double distance;

	@Override
	public void initialize(EffectEntity entity) {
		LivingEntity source = entity.getSource();
		entity.setMovementData(source.getPosition().angleToward(source.getTargetPosition()));
		entity.setForward(true);
		updatePosition(entity);
	}

	@Override
	public void update(EffectEntity entity) {
		updatePosition(entity);
		entity.getVelocity().set(entity.getSource().getVelocity());
	}

	private void updatePosition(EffectEntity entity) {
		entity.getPosition().set(entity.getSource().getPosition()).addEuclidean(distance, (double) entity.getMovementData());
	}

	@Override
	public double getSpeedPotential(EffectEntity entity) {
		return entity.getSource().getSpeed();
	}

}
