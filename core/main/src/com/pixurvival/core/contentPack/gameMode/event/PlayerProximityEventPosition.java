package com.pixurvival.core.contentPack.gameMode.event;

import java.util.Collection;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.CollectionUtils;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerProximityEventPosition implements EventPosition {

	private double distance;

	@Override
	public void apply(World world, Collection<PlayerEntity> players, Vector2 positionOut, Vector2 targetOut) {
		if (players.isEmpty()) {
			return;
		}
		PlayerEntity randomPlayer = CollectionUtils.get(players, world.getRandom().nextInt(players.size()));
		positionOut.setFromEuclidean(distance, world.getRandom().nextAngle()).add(randomPlayer.getPosition());
		targetOut.set(randomPlayer.getPosition());
	}
}
