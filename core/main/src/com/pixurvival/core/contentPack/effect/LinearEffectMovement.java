package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinearEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	@AllArgsConstructor
	private static class MovementData {
		Vector2 targetPosition;
		double targetDistance;
	}

	private double speed;

	private boolean destroyAtTargetPosition;

	@Override
	public void initialize(EffectEntity entity) {
		TeamMember ancestor = entity.getAncestor();
		entity.getPosition().set(ancestor.getPosition());
		entity.setForward(true);
		if (entity.getWorld().isServer() && destroyAtTargetPosition) {
			double d = entity.getWorld().getTime().getDeltaTime() * speed;
			entity.setMovementData(new MovementData(ancestor.getTargetPosition().copy(), d * d));
		}
	}

	@Override
	public void update(EffectEntity entity) {
		if (entity.getWorld().isServer()) {
			MovementData data = (MovementData) entity.getMovementData();
			if (destroyAtTargetPosition && entity.getPosition().distanceSquared(data.targetPosition) <= data.targetDistance) {
				entity.setAlive(false);
			}
		}
	}

	@Override
	public double getSpeedPotential(EffectEntity entity) {
		return speed;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
		// Nothing
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
		// Nothing
	}
}
