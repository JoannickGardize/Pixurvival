package com.pixurvival.core.contentPack.gameMode.role;

import com.pixurvival.core.livingEntity.PlayerEntity;

public class TeamSurvivedWinCondition implements WinCondition {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean test(PlayerEntity playerEntity) {
		return !playerEntity.getTeam().getAliveMembers().isEmpty();
	}

}
