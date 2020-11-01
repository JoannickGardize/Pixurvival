package com.pixurvival.core.contentPack.gameMode.role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.util.IntUrn;
import com.pixurvival.core.util.MathUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Roles implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum SelectionMode {
		LOBBY,
		RANDOM
	}

	private SelectionMode selectionMode = SelectionMode.LOBBY;

	private List<Role> roles = new ArrayList<>();

	public void apply(World world) {
		randomlyAffect(world);
		applyStarterKits(world);
	}

	private void randomlyAffect(World world) {
		if (!roles.isEmpty() && selectionMode == SelectionMode.RANDOM) {
			for (Team team : world.getTeamSet()) {
				randomlyAffect(world, team);
			}
		}
	}

	private void applyStarterKits(World world) {
		for (PlayerEntity player : world.getPlayerEntities().values()) {
			if (player.getRole() == null) {
				continue;
			}
			player.getRole().getStarterKit().apply(player);
		}
	}

	private void randomlyAffect(World world, Team team) {
		int teamSize = team.aliveMemberCount();
		IntUrn urn = new IntUrn(teamSize);
		PlayerEntity[] players = team.getAliveMembers().toArray(new PlayerEntity[teamSize]);
		for (int i = 0; i < roles.size() - 1 && !urn.isEmpty(); i++) {
			Role role = roles.get(i);
			int roleCount = MathUtils.clamp(Math.round(role.getRecommandedRatioPerTeam() * teamSize), role.getMinimumPerTeam(), role.getMaximumPerTeam());
			for (int j = 0; j < roleCount && !urn.isEmpty(); j++) {
				players[urn.draw(world.getRandom())].setRole(role);
			}
		}
		Role lastRole = roles.get(roles.size() - 1);
		while (!urn.isEmpty()) {
			players[urn.draw(world.getRandom())].setRole(lastRole);
		}
	}
}
