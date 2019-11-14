package com.pixurvival.core.livingEntity.alteration;

import java.util.function.Consumer;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FixedMovementAlteration extends PersistentAlteration {

	private static final long serialVersionUID = 1L;

	private AlterationTarget sourceType;
	private SourceDirection sourceDirection;
	private double relativeAngle;
	private double randomAngle;

	private StatFormula speed = new StatFormula();

	@Override
	public Object begin(TeamMember source, LivingEntity entity) {
		TeamMember effectiveSource = sourceType.getFunction().apply(source, entity);
		if (effectiveSource instanceof LivingEntity) {
			((LivingEntity) effectiveSource).prepareTargetedAlteration();
		}
		double angle = sourceDirection.getDirection(effectiveSource, entity);
		double speedValue = speed.getValue(effectiveSource.getStats());
		entity.setFixedMovement(angle + relativeAngle + entity.getWorld().getRandom().nextAngle(randomAngle), speedValue);
		return null;
	}

	@Override
	public void end(TeamMember source, LivingEntity entity, Object data) {
		entity.stopFixedMovement();
	}

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(speed);
	}

}
