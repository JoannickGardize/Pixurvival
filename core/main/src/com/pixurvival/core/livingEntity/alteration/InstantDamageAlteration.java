package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstantDamageAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	private StatAmount amount = new StatAmount();

	@Override
	public void uniqueApply(TeamMember source, LivingEntity entity) {
		entity.takeDamage(amount.getValue(source));
	}
}
