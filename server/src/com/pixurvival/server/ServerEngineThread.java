package com.pixurvival.server;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EngineThread;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.PlayerData;

import lombok.NonNull;

public class ServerEngineThread extends EngineThread {

	private @NonNull ServerGame game;
	private double sendUpdateIntervalMillis = 50;
	private double sendUpdateTimer = 0;
	private List<GameSession> sessions = new ArrayList<>();
	private List<PlayerData> tmpPlayerData = new ArrayList<>();

	public ServerEngineThread(ServerGame game) {
		super("Main Server Thread");
		this.game = game;
	}

	public void add(GameSession worldSession) {
		sessions.add(worldSession);
	}

	@Override
	public void update(double deltaTimeMillis) {
		game.consumeReceivedObjects();
		sessions.forEach(w -> w.getWorld().update(deltaTimeMillis));
		sendUpdateTimer += deltaTimeMillis;
		if (sendUpdateTimer > sendUpdateIntervalMillis) {
			sessions.forEach(gs -> {
				gs.getWorld().incrementUpdateId();
				gs.foreachPlayers(p -> {
					PlayerConnection connection = p.getConnection();
					if (connection.isPlayerDataChanged()) {
						tmpPlayerData.add(connection.getPlayerEntity().getData());
						connection.setPlayerDataChanged(false);
					}
				});
				PlayerData[] playerData = null;
				if (!tmpPlayerData.isEmpty()) {
					playerData = tmpPlayerData.toArray(new PlayerData[tmpPlayerData.size()]);
				}
				final PlayerData[] finalPlayerData = playerData;
				gs.foreachPlayers(p -> {
					PlayerConnection connection = p.getConnection();
					PlayerEntity playerEntity = connection.getPlayerEntity();
					if (connection.isGameReady() && playerEntity != null) {
						playerEntity.getWorld().writeWorldUpdateFor(playerEntity);
						playerEntity.getWorld().getWorldUpdate().setStructureUpdates(p.pollStructureUpdatesToSend());
						playerEntity.getWorld().getWorldUpdate().setCompressedChunks(p.pollChunksToSend());
						connection.sendUDP(playerEntity.getWorld().getWorldUpdate());
						if (connection.isInventoryChanged()) {
							connection.setInventoryChanged(false);
							connection.sendTCP(playerEntity.getInventory());
						}
					}
					if (finalPlayerData != null) {
						connection.sendTCP(finalPlayerData);
					}
				});
			});
			if (sendUpdateTimer > sendUpdateIntervalMillis * 1.5) {
				sendUpdateTimer = 0;
				Log.warn("Some frames send skipped.");
			} else {
				sendUpdateTimer -= sendUpdateIntervalMillis;
			}
		}
	}

	@Override
	protected void frameSkipped() {
		Log.warn("Server frames skipped.");
	}
}
