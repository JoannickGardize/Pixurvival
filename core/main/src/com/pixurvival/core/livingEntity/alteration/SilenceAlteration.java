package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.TeamMember;

public class SilenceAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	private long duration;

	@Override
	public void apply(TeamMember source, LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			((PlayerEntity) entity).silence(duration);
		}
	}

}
