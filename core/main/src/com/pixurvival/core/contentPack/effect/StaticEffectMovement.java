package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.team.TeamMember;

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
		TeamMember ancestor = entity.getAncestor();
		double distanceSquared = ancestor.getPosition().distanceSquared(ancestor.getTargetPosition());
		if (distanceSquared <= minDistance * minDistance) {
			entity.getPosition().set(ancestor.getPosition()).addEuclidean(minDistance, entity.getMovingAngle());
		} else if (distanceSquared >= maxDistance * maxDistance) {
			entity.getPosition().set(ancestor.getPosition()).addEuclidean(maxDistance, entity.getMovingAngle());
		} else {
			entity.getPosition().set(ancestor.getTargetPosition());
		}
	}

	@Override
	public void update(EffectEntity entity) {
		// Nothing
	}

	@Override
	public double getSpeedPotential(EffectEntity entity) {
		return 0;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
	}
}
