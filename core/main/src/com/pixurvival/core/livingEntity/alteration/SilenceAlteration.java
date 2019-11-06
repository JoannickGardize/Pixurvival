package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

public class SilenceAlteration extends Alteration {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private long duration;

	@Override
	public void targetedApply(TeamMember source, LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			((PlayerEntity) entity).silence(duration);
		}
	}
}
