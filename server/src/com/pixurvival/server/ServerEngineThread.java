package com.pixurvival.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EngineThread;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityCollection;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.message.PlayerDead;
import com.pixurvival.core.message.WorldUpdate;

import lombok.NonNull;

public class ServerEngineThread extends EngineThread {

	private @NonNull ServerGame game;
	private double sendUpdateIntervalMillis = 50;
	private double sendUpdateTimer = 0;
	private List<GameSession> sessions = new ArrayList<>();
	private WorldUpdate worldUpdate = new WorldUpdate();
	private List<SpectatorSession> tmpNewSpectators = new ArrayList<>();
	private EntityCollection tmpRemoveEntityCollection = new EntityCollection();

	public ServerEngineThread(ServerGame game) {
		super("Main Server Thread");
		this.game = game;
		// setWarnLoadTrigger(0.8);
	}

	public synchronized void add(GameSession worldSession) {
		sessions.add(worldSession);
	}

	@Override
	public synchronized void update(double deltaTimeMillis) {
		game.consumeReceivedObjects();
		sessions.forEach(gs -> gs.getWorld().update(deltaTimeMillis));
		sendUpdateTimer += deltaTimeMillis;
		if (sendUpdateTimer > sendUpdateIntervalMillis) {
			sessions.forEach(gs -> {
				worldUpdate.setUpdateId(worldUpdate.getUpdateId() + 1);
				sendWorldData(gs);
				gs.clear();

				gs.getWorld().getEntityPool().foreach(entity -> entity.setStateChanged(false));
			});
			sessions.forEach(gs -> gs.getWorld().getEntityPool().foreach(entity -> {
				if (entity.getChunk() != null) {
					entity.setPreviousUpdateChunkPosition(entity.getChunk().getPosition());
				}
			}));
			if (sendUpdateTimer > sendUpdateIntervalMillis * 1.5) {
				sendUpdateTimer = 0;
				Log.warn("Some frames send skipped.");
			} else {
				sendUpdateTimer -= sendUpdateIntervalMillis;
			}
		}
	}

	private void sendWorldData(GameSession gs) {
		PlayerDead[] playerDeads = gs.extractPlayerDeads();
		gs.foreachPlayers(session -> {
			PlayerConnection connection = session.getConnection();
			PlayerEntity playerEntity = connection.getPlayerEntity();
			if (connection.isGameReady() && playerEntity != null) {
				worldUpdate.clear();
				ByteBuffer byteBuffer = worldUpdate.getEntityUpdateByteBuffer();
				writeRemoveEntity(gs, session, byteBuffer);
				byteBuffer.put(EntityGroup.END_MARKER);
				writeEntityUpdate(session, playerEntity, byteBuffer);
				byteBuffer.put(EntityGroup.END_MARKER);
				writeDistantAllyPositions(playerEntity, byteBuffer);
				session.extractStructureUpdatesToSend(worldUpdate.getStructureUpdates());
				session.extractChunksToSend(worldUpdate.getCompressedChunks());
				if (byteBuffer.position() > 4 || !worldUpdate.getStructureUpdates().isEmpty() || !worldUpdate.getCompressedChunks().isEmpty()) {
					Log.debug("sendUDP to " + connection + " worldUpdate of size " + byteBuffer.position());
					connection.sendUDP(worldUpdate);
					tmpNewSpectators.clear();
					session.getSpectators().values().forEach(spec -> {
						if (spec.isNewlySpectating()) {
							tmpNewSpectators.add(spec);
						} else {
							spec.getConnection().sendUDP(worldUpdate);
						}
						if (connection.isInventoryChanged()) {
							spec.getConnection().sendTCP(playerEntity.getInventory());
						}
						if (playerDeads != null) {
							spec.getConnection().sendTCP(playerDeads);
						}
					});
					sendFullUpdateForNewSpectators(playerEntity);
					game.notifyNetworkListeners(l -> l.sent(worldUpdate));
				}
				if (connection.isInventoryChanged()) {
					connection.setInventoryChanged(false);
					connection.sendTCP(playerEntity.getInventory());
				}
				if (playerDeads != null) {
					connection.sendTCP(playerDeads);
				}
			}
			session.clearPositionChanges();
		});
	}

	public void sendFullUpdateForNewSpectators(PlayerEntity viewedPlayer) {
		if (tmpNewSpectators.isEmpty()) {
			return;
		}
		worldUpdate.clear();
		ByteBuffer byteBuffer = worldUpdate.getEntityUpdateByteBuffer();
		byteBuffer.put(EntityGroup.REMOVE_ALL_MARKER);
		byteBuffer.put(EntityGroup.END_MARKER);
		viewedPlayer.foreachChunkInView(chunk -> {
			chunk.getEntities().writeUpdate(byteBuffer, true, viewedPlayer.getChunkVision());
			worldUpdate.getCompressedChunks().add(chunk.getCompressed());
		});
		byteBuffer.put(EntityGroup.END_MARKER);
		writeDistantAllyPositions(viewedPlayer, byteBuffer);
		tmpNewSpectators.forEach(spec -> {
			spec.getConnection().sendUDP(worldUpdate);
			spec.getConnection().sendTCP(viewedPlayer.getInventory());
			spec.setNewlySpectating(false);
		});
	}

	private void writeEntityUpdate(PlayerSession session, PlayerEntity playerEntity, ByteBuffer byteBuffer) {
		playerEntity.foreachChunkInView(chunk -> chunk.getEntities().writeUpdate(byteBuffer, session.isNewPosition(chunk.getPosition()), playerEntity.getChunkVision()));
	}

	private void writeRemoveEntity(GameSession gs, PlayerSession session, ByteBuffer byteBuffer) {
		PlayerConnection connection = session.getConnection();
		PlayerEntity playerEntity = connection.getPlayerEntity();
		TiledMap map = playerEntity.getWorld().getMap();
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
	}

	private void writeDistantAllyPositions(PlayerEntity player, ByteBuffer byteBuffer) {
		int lengthPosition = byteBuffer.position();
		byteBuffer.position(byteBuffer.position() + 2);
		short length = 0;
		for (Entity ally : player.getTeam().getAliveMembers()) {
			if (ally != player && ally.getChunk() != null && !ally.getChunk().getPosition().insideSquare(player.getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
				byteBuffer.putLong(ally.getId());
				byteBuffer.putDouble(ally.getPosition().getX());
				byteBuffer.putDouble(ally.getPosition().getY());
				byteBuffer.putDouble(ally.getVelocity().getX());
				byteBuffer.putDouble(ally.getVelocity().getY());
				length++;
			}
		}
		byteBuffer.putShort(lengthPosition, length);
	}

	@Override
	protected void frameSkipped() {
		Log.warn("Server frames skipped.");
	}
}
