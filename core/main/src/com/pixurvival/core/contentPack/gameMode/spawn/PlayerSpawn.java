package com.pixurvival.core.contentPack.gameMode.spawn;

import java.io.Serializable;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.analytics.CardinalDirection;
import com.pixurvival.core.map.analytics.MapAnalyticsException;
import com.pixurvival.core.team.Team;
import com.pixurvival.core.util.Vector2;

public interface PlayerSpawn extends Serializable {

	void apply(World world) throws MapAnalyticsException;

	default void spawnTeam(Team team, Vector2 spawnPosition) {
		CardinalDirection currentDirection = CardinalDirection.EAST;
		spawnPosition.setX((int) spawnPosition.getX() + 0.5f);
		spawnPosition.setY((int) spawnPosition.getY() + 0.5f);
		for (PlayerEntity player : team) {
			player.getPosition().set(spawnPosition);
			TiledMap map = player.getWorld().getMap();
			for (int j = 0; j < 4; j++) {
				if (map.tileAt((int) spawnPosition.getX() + currentDirection.getNormalX(), (int) spawnPosition.getY() + currentDirection.getNormalY()).isSolid()) {
					currentDirection = currentDirection.getNext();
				} else {
					spawnPosition.addX(currentDirection.getNormalX());
					spawnPosition.addY(currentDirection.getNormalY());
					break;
				}
			}
		}
	}
}
