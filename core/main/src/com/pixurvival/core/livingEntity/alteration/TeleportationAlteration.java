package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

public class TeleportationAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	@Override
	public void targetedApply(TeamMember source, LivingEntity entity) {
		entity.teleport(source.getPosition());
	}

}