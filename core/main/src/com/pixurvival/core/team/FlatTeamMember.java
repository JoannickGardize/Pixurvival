package com.pixurvival.core.team;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.stats.StatSet;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class FlatTeamMember implements TeamMember {

	private Vector2 position = new Vector2();
	private @NonNull World world;
	private @Setter Team team = TeamSet.WILD_TEAM;
	private StatSet stats = new StatSet();
	private Vector2 targetPosition = new Vector2();

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public TeamMember getOrigin() {
		return this;
	}

	@Override
	public boolean isAlive() {
		return true;
	}
}
