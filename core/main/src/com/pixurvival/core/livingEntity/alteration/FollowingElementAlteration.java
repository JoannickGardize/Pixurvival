package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.contentPack.effect.FollowingElement;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowingElementAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	public enum FollowingElementSource {
		ORIGIN,
		SELF
	}

	private FollowingElementSource source = FollowingElementSource.SELF;
	private FollowingElement followingElement;

	@Override
	public void uniqueApply(TeamMember source, LivingEntity entity) {
		followingElement.apply(this.source == FollowingElementSource.ORIGIN ? source.getOrigin() : source);
	}
}