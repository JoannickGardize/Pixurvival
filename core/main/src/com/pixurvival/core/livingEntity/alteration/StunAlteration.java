package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StunAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	private long duration;

	@Override
	public void targetedApply(TeamMember source, TeamMember entity) {
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).stun(duration);
		}
	}

}
