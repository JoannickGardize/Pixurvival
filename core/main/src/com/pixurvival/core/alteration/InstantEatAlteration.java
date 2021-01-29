package com.pixurvival.core.alteration;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstantEatAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	@Valid
	private StatFormula amount = new StatFormula();

	@Override
	public void targetedApply(TeamMember source, TeamMember entity) {
		if (entity instanceof PlayerEntity) {
			((PlayerEntity) entity).addHunger(amount.getValue(source));
		}
	}

}
