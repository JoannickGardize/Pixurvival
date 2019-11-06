package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.team.TeamMember;

public enum SourceDirection {

	POSITION {
		@Override
		public double getDirection(TeamMember source, TeamMember target) {
			return source.getPosition().angleToward(target.getPosition());
		}
	},
	TARGET {
		@Override
		public double getDirection(TeamMember source, TeamMember target) {
			return source.getPosition().angleToward(source.getTargetPosition());
		}
	};

	public abstract double getDirection(TeamMember source, TeamMember target);
}
