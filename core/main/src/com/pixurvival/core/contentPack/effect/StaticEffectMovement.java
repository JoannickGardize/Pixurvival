package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaticEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	private double minimumDistance;
	private double maximumDistance;

	@Override
	public void initialize(EffectEntity entity) {
		LivingEntity source = entity.getSource();
		double angle = source.getPosition().angleToward(source.getTargetPosition());
		double distance = source.getPosition().distanceSquared(source.getPosition());
		entity.getPosition().set(source.getPosition()).addEuclidean(distance, angle);
	}

	@Override
	public void update(EffectEntity entity) {
		// Nothing
	}

	@Override
	public double getSpeedPotential() {
		return 0;
	}
}
