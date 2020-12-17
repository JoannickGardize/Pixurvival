package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DelayedAlteration extends PersistentAlteration {

	private static final long serialVersionUID = 1L;

	@Valid
	private Alteration alteration;

	@Override
	public void end(TeamMember source, LivingEntity target, Object data) {
		alteration.apply(source, target);
	}
}
