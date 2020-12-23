package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.gameMode.role.Role;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.Team;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemainingRolesEndCondition extends PlayerAliveCountEndGameCondition {

	private static final long serialVersionUID = 1L;

	@ElementReference("<<.roles.roles")
	private List<Role> roles = new ArrayList<>();

	private boolean countPerTeam = false;

	@Positive
	private int value;

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
			return countRoles((Collection) world.getEntityPool().get(EntityGroup.PLAYER)) <= value;
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
