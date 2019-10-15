package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FixedMovementAlteration extends PersistentAlteration {

	private static final long serialVersionUID = 1L;

	public enum FixedMovementOrigin {
		SOURCE,
		SELF;
	}

	private FixedMovementOrigin origin;
	private double relativeAngle;
	private double randomAngle;

	private StatAmount speed = new StatAmount();

	@Override
	public Object begin(TeamMember source, LivingEntity entity) {
		double targetAngle;
		double speedValue;
		if (origin == FixedMovementOrigin.SOURCE) {
			targetAngle = source.getPosition().angleToward(source.getTargetPosition());
			speedValue = speed.getValue(source.getStats());
		} else {
			targetAngle = entity.getPosition().angleToward(entity.getTargetPosition());
			speedValue = speed.getValue(entity.getStats());
		}
		entity.setFixedMovement(targetAngle + relativeAngle + entity.getWorld().getRandom().nextAngle(randomAngle), speedValue);
		return null;
	}

	@Override
	public void end(TeamMember source, LivingEntity entity, Object data) {
		entity.stopFixedMovement();
	}

}
