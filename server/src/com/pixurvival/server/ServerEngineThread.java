package com.pixurvival.server;

import com.pixurvival.core.EngineThread;
import com.pixurvival.core.World;

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
				w.writeEntitiesUpdate();
			});
			game.foreachPlayers(p -> {
				if (p.isGameReady() && p.getPlayerEntity() != null) {
					p.sendUDP(p.getPlayerEntity().getWorld().getEntitiesUpdate());
					if (p.isInventoryChanged()) {
						p.setInventoryChanged(false);
						p.sendUDP(p.getPlayerEntity().getInventory());
					}
				}
			});
			if (sendUpdateTimer > sendUpdateIntervalMillis * 1.5) {
				sendUpdateTimer = 0;
			} else {
				sendUpdateTimer -= sendUpdateIntervalMillis;
			}
		}
	}
}
