package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstantEatAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	private StatAmount amount = new StatAmount();

	@Override
	public void apply(TeamMember source, LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			((PlayerEntity) entity).addHunger(amount.getValue(source));
		}
	}

}
