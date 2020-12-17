package com.pixurvival.core.contentPack.gameMode.spawn;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.util.IntUrn;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaticPlayerSpawn implements PlayerSpawn {

	private static final long serialVersionUID = 1L;

	@Valid
	@Length(min = 1)
	private List<Vector2> positions = new ArrayList<>();

	@Override
	public void apply(World world) {
		IntUrn urn = new IntUrn(positions.size());
		for (Team team : world.getTeamSet()) {
			if (team.isPlayerTeam()) {
				spawnTeam(team, positions.get(urn.draw(world.getRandom())));
				if (urn.isEmpty()) {
					urn.reset();
				}
			}
		}
		Vector2 spawnCenter = new Vector2();
		for (Vector2 position : positions) {
			spawnCenter.add(position);
		}
		spawnCenter.div(positions.size());
		world.setSpawnCenter(spawnCenter);
	}
}
