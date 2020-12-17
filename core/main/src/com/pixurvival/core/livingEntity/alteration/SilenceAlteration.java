package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

public class SilenceAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	@Positive
	private long duration;

	@Override
	public void targetedApply(TeamMember source, TeamMember entity) {
		if (entity instanceof LivingEntity) {
			((LivingEntity) entity).silence(duration);
		}
	}
}
