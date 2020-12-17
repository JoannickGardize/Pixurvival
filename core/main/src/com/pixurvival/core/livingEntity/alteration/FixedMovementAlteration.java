package com.pixurvival.core.livingEntity.alteration;

import java.util.function.Consumer;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
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
	private float relativeAngle;
	private float randomAngle;
	@Valid
	private StatFormula speed = new StatFormula();

	@Override
	public Object begin(TeamMember source, LivingEntity target) {
		TeamMember effectiveSource = sourceType.getFunction().apply(source, target);
		if (effectiveSource instanceof LivingEntity) {
			((LivingEntity) effectiveSource).prepareTargetedAlteration();
		}
		float angle = sourceDirection.getDirection(effectiveSource, target);
		float speedValue = speed.getValue(effectiveSource.getStats());
		target.setFixedMovement(angle + relativeAngle + target.getWorld().getRandom().nextAngle(randomAngle), speedValue);
		return null;
	}

	@Override
	public void end(TeamMember source, LivingEntity target, Object data) {
		target.stopFixedMovement();
	}

	@Override
	public void forEachStatFormulas(Consumer<StatFormula> action) {
		action.accept(speed);
	}

}
