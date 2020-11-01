package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.gameMode.role.Role;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.Team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemainingRolesEndCondition extends PlayerAliveCountEndGameCondition {

	private static final long serialVersionUID = 1L;

	private List<Role> roles = new ArrayList<>();

	private boolean countPerTeam = false;

	private int value;

	@Override
	protected boolean compute(World world) {
		if (countPerTeam) {
			for (Team team : world.getTeamSet()) {
				if (countRoles(team.getAliveMembers()) <= value) {
					return true;
				}
			}
			return false;
		} else {
			return countRoles(world.getPlayerEntities().values()) <= value;
		}
	}

	public int countRoles(Collection<PlayerEntity> collection) {
		int count = 0;
		for (PlayerEntity player : collection) {
			if (roles.contains(player.getRole())) {
				count++;
			}
		}
		return count;
	}
}
