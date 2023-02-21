package com.pixurvival.core.alteration.condition;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.FloatComparison;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthAlterationCondition implements AlterationCondition {

	private static final long serialVersionUID = 1L;

	private FloatComparison operator;
	@Bounds(min = 0, max = 1, maxInclusive = true)
	private float percentValue;

	@Override
	public boolean test(TeamMember entity) {
		if (entity instanceof Damageable) {
			return operator.test(((Damageable) entity).getPercentHealth(), percentValue);
		} else {
			return false;
		}
	}
}
