package com.pixurvival.core.contentPack.gameMode.event;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.team.TeamSet;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EffectEventTeamMember implements TeamMember {

	private @Getter @NonNull World world;

	private @Getter Vector2 position = new Vector2();
	private @Getter Vector2 targetPosition = new Vector2();
	private @Getter StatSet stats = new StatSet();

	@Override
	public Team getTeam() {
		return TeamSet.WILD_TEAM;
	}

	@Override
	public TeamMember getOrigin() {
		return this;
	}

	@Override
	public TeamMember findIfNotFound() {
		return this;
	}

	@Override
	public boolean isAlive() {
		return true;
	}

}
