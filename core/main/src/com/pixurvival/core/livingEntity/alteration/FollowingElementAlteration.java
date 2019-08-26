package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingElementAlteration implements Alteration {

	private static final long serialVersionUID = 1L;

	private FollowingElement followingElement;

	@Override
	public void apply(TeamMember source, LivingEntity entity) {
		if (source instanceof EffectEntity) {
			followingElement.apply((EffectEntity) source);
		}
	}
}
