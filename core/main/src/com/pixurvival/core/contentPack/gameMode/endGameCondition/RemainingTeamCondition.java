package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import com.pixurvival.core.World;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityPoolListener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.Team;

import lombok.Data;

@Data
public class RemainingTeamCondition implements EndGameCondition, EntityPoolListener {

	private static final long serialVersionUID = 1L;

	private int remainingTeamCondition = 1;

	@Override
	public void initialize(World world) {
		world.getEntityPool().addListener(this);
		world.setEndGameConditionData(false);
	}

	@Override
	public boolean update(World world) {
		return (boolean) world.getEndGameConditionData();
	}

	@Override
	public void entityAdded(Entity e) {
		// Nothing
	}

	@Override
	public void entityRemoved(Entity e) {
		if (!(e instanceof PlayerEntity)) {
			return;
		}
		World world = e.getWorld();
		int remainingTeam = 0;
		for (Team team : world.getTeamSet()) {
			if (team.aliveMemberCount() > 0) {
				remainingTeam++;
			}
		}
		if (remainingTeam <= remainingTeamCondition) {
			world.setEndGameConditionData(true);
		}
	}

	@Override
	public void sneakyEntityRemoved(Entity e) {
	}
}