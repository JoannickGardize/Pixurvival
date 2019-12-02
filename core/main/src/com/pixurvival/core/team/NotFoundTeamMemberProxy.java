package com.pixurvival.core.team;

import com.pixurvival.core.World;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class NotFoundTeamMemberProxy implements TeamMember {

	private @Getter World world;
	private EntityGroup group;
	private long id;

	@Override
	public Vector2 getPosition() {
		return new Vector2();
	}

	@Override
	public Team getTeam() {
		return TeamSet.WILD_TEAM;
	}

	@Override
	public StatSet getStats() {
		return new StatSet();
	}

	@Override
	public Vector2 getTargetPosition() {
		return new Vector2();
	}

	@Override
	public TeamMember getOrigin() {
		return this;
	}

	@Override
	public TeamMember findIfNotFound() {
		return (TeamMember) world.getEntityPool().get(group, id);
	}

}
