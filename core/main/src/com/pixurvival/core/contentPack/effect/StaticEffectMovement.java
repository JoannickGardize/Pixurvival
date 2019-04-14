package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.util.MathUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaticEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	private double minDistance;
	private double maxDistance;

	@Override
	public void initialize(EffectEntity entity) {
		LivingEntity source = entity.getSource();
		double angle = source.getPosition().angleToward(source.getTargetPosition());
		double distance = source.getPosition().distanceSquared(source.getTargetPosition());
		distance = MathUtils.clamp(distance, minDistance, maxDistance);
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
