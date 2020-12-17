package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.team.Team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemainingTeamEndCondition extends PlayerAliveCountEndGameCondition {

	private static final long serialVersionUID = 1L;

	@Positive
	private int remainingTeamCondition = 1;

	@Override
	protected boolean compute(World world) {
		int remainingTeam = 0;
		for (Team team : world.getTeamSet()) {
			if (team.aliveMemberCount() > 0) {
				remainingTeam++;
			}
		}
		return remainingTeam <= remainingTeamCondition;
	}
}