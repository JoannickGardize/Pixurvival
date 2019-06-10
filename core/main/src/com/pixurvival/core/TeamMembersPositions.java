package com.pixurvival.core;

import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Plugin;

public class TeamMembersPositions implements Plugin<World> {

	private Map<Long, PlayerEntity> members = new HashMap<>();

	@Override
	public void update(World world) {
		PlayerEntity myPlayer = world.getMyPlayer();
		if (myPlayer == null) {
			return;
		}
	}

}
