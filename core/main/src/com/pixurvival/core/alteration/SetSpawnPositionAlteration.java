package com.pixurvival.core.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

public class SetSpawnPositionAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	@Override
	public void uniqueApply(TeamMember source, TeamMember entity) {
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).getSpawnPosition().set(entity.getPosition());
		}
	}
}
