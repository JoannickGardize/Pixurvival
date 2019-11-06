package com.pixurvival.core.contentPack.effect;

import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingEffect extends FollowingElement {

	private static final long serialVersionUID = 1L;

	private OffsetAngleEffect offsetAngleEffect;

	@Override
	public void apply(TeamMember origin) {
		EffectEntity following = new EffectEntity(offsetAngleEffect, origin);
		origin.getWorld().getEntityPool().add(following);
	}
}
