package com.pixurvival.server;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EngineThread;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.message.HarvestableStructureUpdate;

import lombok.NonNull;

public class ServerEngineThread extends EngineThread {

	private @NonNull ServerGame game;
	private double sendUpdateIntervalMillis = 50;
	private double sendUpdateTimer = 0;
	private List<WorldSession> worldSessions = new ArrayList<>();

	public ServerEngineThread(ServerGame game) {
		super("Main Server Thread");
		this.game = game;
	}

	public void add(WorldSession worldSession) {
		worldSessions.add(worldSession);
	}

	@Override
	public void update(double deltaTimeMillis) {
		game.consumeReceivedObjects();
		worldSessions.forEach(w -> w.getWorld().update(deltaTimeMillis));
		sendUpdateTimer += deltaTimeMillis;
		if (sendUpdateTimer > sendUpdateIntervalMillis) {
			worldSessions.forEach(w -> {
				w.getWorld().incrementUpdateId();
				HarvestableStructureUpdate[] structureUpdates = w.consumeStructureUpdates();
				w.getPlayers().forEach(p -> {
					PlayerEntity playerEntity = p.getPlayerEntity();
					if (p.isGameReady() && playerEntity != null) {
						playerEntity.getWorld().writeEntitiesUpdateFor(playerEntity);
						p.getPlayerEntity().getWorld().getEntitiesUpdate().setStructureUpdates(structureUpdates);
						p.sendUDP(p.getPlayerEntity().getWorld().getEntitiesUpdate());
						if (p.isInventoryChanged()) {
							p.setInventoryChanged(false);
							p.sendUDP(p.getPlayerEntity().getInventory());
						}
					}
				});
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
