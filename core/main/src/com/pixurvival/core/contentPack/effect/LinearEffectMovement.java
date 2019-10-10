package com.pixurvival.core.contentPack.effect;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinearEffectMovement implements EffectMovement {

	private static final long serialVersionUID = 1L;

	private static class MovementData {
		Vector2 targetPosition;
		double targetDistance;
	}

	private double initialDistance;
	private double speed;
	private boolean relative;
	private boolean destroyAtTargetPosition;

	@Override
	public void initialize(EffectEntity entity) {
		TeamMember ancestor = entity.getAncestor();
		double angle = ancestor.getPosition().angleToward(ancestor.getTargetPosition()) + entity.getDefinition().getOffsetAngle()
				+ entity.getWorld().getRandom().nextAngle(entity.getDefinition().getRandomAngle());
		entity.getPosition().set(ancestor.getPosition());
		if (initialDistance > 0) {
			entity.getPosition().add(Vector2.fromEuclidean(initialDistance, angle));
		}
		entity.setForward(true);
		MovementData data = new MovementData();
		entity.setMovementData(data);
		if (relative && ancestor instanceof Entity && ((Entity) ancestor).isForward()) {
			Vector2 velocityVector = Vector2.fromEuclidean(speed, angle).add(((Entity) ancestor).getTargetVelocity());
			entity.setVelocityDirect(velocityVector);
		} else {
			entity.setSpeed(speed);
			entity.setMovingAngle(angle);
			entity.updateVelocity();
		}
		if (entity.getWorld().isServer() && destroyAtTargetPosition) {
			double d = entity.getWorld().getTime().getDeltaTime() * entity.getSpeed();
			data.targetPosition = ancestor.getTargetPosition().copy();
			data.targetDistance = d * d;
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
		return entity.isAlive() ? entity.getSpeed() : 0;
	}

	@Override
	public void writeUpdate(ByteBuffer buffer, EffectEntity entity) {
		buffer.putDouble(entity.getSpeed());
	}

	@Override
	public void applyUpdate(ByteBuffer buffer, EffectEntity entity) {
		entity.setSpeed(buffer.getDouble());
	}
}
