package com.pixurvival.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EngineThread;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.entity.EntityCollection;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.PlayerData;
import com.pixurvival.core.message.WorldUpdate;

import lombok.NonNull;

public class ServerEngineThread extends EngineThread {

	private @NonNull ServerGame game;
	private double sendUpdateIntervalMillis = 50;
	private double sendUpdateTimer = 0;
	private List<GameSession> sessions = new ArrayList<>();
	private List<PlayerData> tmpPlayerData = new ArrayList<>();
	private WorldUpdate worldUpdate = new WorldUpdate();
	private EntityCollection tmpRemoveEntityCollection = new EntityCollection();

	public ServerEngineThread(ServerGame game) {
		super("Main Server Thread");
		this.game = game;
		// setWarnLoadTrigger(0.8);
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
				worldUpdate.setUpdateId(worldUpdate.getUpdateId() + 1);
				sendWorldData(gs, buildPlayerData(gs));
				gs.getRemovedEntities().clear();
				gs.getChunkChangedEntities().clear();
				gs.getWorld().getEntityPool().foreach(entity -> entity.setStateChanged(false));
			});
			if (sendUpdateTimer > sendUpdateIntervalMillis * 1.5) {
				sendUpdateTimer = 0;
				Log.warn("Some frames send skipped.");
			} else {
				sendUpdateTimer -= sendUpdateIntervalMillis;
			}
		}
	}

	private PlayerData[] buildPlayerData(GameSession gs) {
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
			tmpPlayerData.clear();
		}
		return playerData;
	}

	private void sendWorldData(GameSession gs, PlayerData[] finalPlayerData) {
		gs.foreachPlayers(session -> {
			PlayerConnection connection = session.getConnection();
			PlayerEntity playerEntity = connection.getPlayerEntity();
			TiledMap map = playerEntity.getWorld().getMap();
			if (connection.isGameReady() && playerEntity != null) {
				worldUpdate.clear();
				ByteBuffer byteBuffer = worldUpdate.getEntityUpdateByteBuffer();
				playerEntity.foreachChunkInView(chunk -> {
					boolean onlyChanged = !session.isNewPosition(chunk.getPosition());
					chunk.getEntities().writeUpdate(byteBuffer, onlyChanged);
				});
				byteBuffer.put(EntityGroup.END_MARKER);
				tmpRemoveEntityCollection.clear();
				session.foreachOldPosition(position -> map.ifChunkExists(position, chunk -> tmpRemoveEntityCollection.addAll(chunk.getEntities())));
				gs.getRemovedEntities().forEach((position, entityCollection) -> {
					if (position.insideSquare(playerEntity.getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
						tmpRemoveEntityCollection.addAll(entityCollection);
					}
				});
				gs.getChunkChangedEntities().forEach((previousPosition, entityList) -> {
					if (previousPosition.insideSquare(playerEntity.getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
						entityList.forEach(e -> {
							if (!playerEntity.getChunkVision().contains(e.getChunk().getPosition())) {
								tmpRemoveEntityCollection.addAll(entityList);
							}
						});
					}
				});
				tmpRemoveEntityCollection.writeAllIds(byteBuffer);
				byteBuffer.put(EntityGroup.END_MARKER);
				session.extractStructureUpdatesToSend(worldUpdate.getStructureUpdates());
				session.extractChunksToSend(worldUpdate.getCompressedChunks());
				if (byteBuffer.position() > 2 || !worldUpdate.getStructureUpdates().isEmpty() || !worldUpdate.getCompressedChunks().isEmpty()) {
					connection.sendUDP(worldUpdate);
				}
				if (connection.isInventoryChanged()) {
					connection.setInventoryChanged(false);
					connection.sendTCP(playerEntity.getInventory());
				}
			}
			if (finalPlayerData != null) {
				connection.sendTCP(finalPlayerData);
			}
			session.clearPositionChanges();
		});
	}

	@Override
	protected void frameSkipped() {
		Log.warn("Server frames skipped.");
	}
}
