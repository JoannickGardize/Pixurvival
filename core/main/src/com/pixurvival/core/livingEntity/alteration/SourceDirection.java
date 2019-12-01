package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.team.TeamMember;

public enum SourceDirection {

	POSITION {
		@Override
		public float getDirection(TeamMember source, TeamMember target) {
			return source.getPosition().angleToward(target.getPosition());
		}
	},
	TARGET {
		@Override
		public float getDirection(TeamMember source, TeamMember target) {
			return source.getPosition().angleToward(source.getTargetPosition());
		}
	};

	public abstract float getDirection(TeamMember source, TeamMember target);
}
