package com.pixurvival.server;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EngineThread;
import com.pixurvival.core.World;
import com.pixurvival.core.aliveEntity.PlayerEntity;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerEngineThread extends EngineThread {

	private @NonNull ServerGame game;
	private double sendUpdateIntervalMillis = 50;
	private double sendUpdateTimer = 0;

	@Override
	public void update(double deltaTimeMillis) {
		game.consumeReceivedObjects();
		World.getWorlds().forEach(w -> w.update(deltaTimeMillis));
		sendUpdateTimer += deltaTimeMillis;
		if (sendUpdateTimer > sendUpdateIntervalMillis) {
			World.getWorlds().forEach(w -> {
				w.incrementUpdateId();
			});
			game.foreachPlayers(p -> {
				PlayerEntity playerEntity = p.getPlayerEntity();
				if (p.isGameReady() && playerEntity != null) {
					playerEntity.getWorld().writeEntitiesUpdateFor(playerEntity);
					p.sendUDP(p.getPlayerEntity().getWorld().getEntitiesUpdate());
					if (p.isInventoryChanged()) {
						p.setInventoryChanged(false);
						p.sendUDP(p.getPlayerEntity().getInventory());
					}
				}
			});
			if (sendUpdateTimer > sendUpdateIntervalMillis * 1.5) {
				sendUpdateTimer = 0;
				Log.warn("Some frames skipped.");
			} else {
				sendUpdateTimer -= sendUpdateIntervalMillis;
			}
		}
	}
}
