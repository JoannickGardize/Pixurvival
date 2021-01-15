package com.pixurvival.core.contentPack.gameMode;

import java.util.function.ToLongFunction;

import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerRespawnAction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PlayerRespawnType {
	NONE(p -> -1),
	SPAWN_POSITION(p -> addRespawnPlayerTimer(p, false)),
	CLOSEST_ALLY(p -> addRespawnPlayerTimer(p, true));

	/**
	 * Returns the world time at which the player will respawn, or -1 if no respawn
	 * is planned.
	 */
	private @Getter ToLongFunction<PlayerEntity> handler;

	private static long addRespawnPlayerTimer(PlayerEntity playerEntity, boolean respawnToAlly) {
		playerEntity.getWorld().getActionTimerManager().addActionTimer(new PlayerRespawnAction(playerEntity.getId(), respawnToAlly), playerEntity.getWorld().getGameMode().getPlayerRespawnDelay());
		return playerEntity.getWorld().getGameMode().getPlayerRespawnDelay() + playerEntity.getWorld().getTime().getTimeMillis();
	}

}
