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
		// TODO Trouver une solution pour synchroniser cette donnée entre client
		// et serveur :
		entity.setMovementData(source.getPosition().angleToward(source.getTargetPosition()));
		updatePosition(entity);
	}

	@Override
	public void update(EffectEntity entity) {
		updatePosition(entity);
		entity.setForward(entity.getSource().isForward());
		entity.setMovingAngle(entity.getSource().getMovingAngle());
	}

	private void updatePosition(EffectEntity entity) {
		entity.getPosition().set(entity.getSource().getPreviousPosition()).addEuclidean(distance, (double) entity.getMovementData());
	}

	@Override
	public double getSpeedPotential(EffectEntity entity) {
		if (entity.getSource() == null) {
			return 0;
		} else {
			return entity.getSource().getSpeed();
		}
	}
}
