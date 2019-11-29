package com.pixurvival.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EngineThread;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityCollection;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.message.PlayerDead;
import com.pixurvival.core.message.WorldUpdate;

import lombok.NonNull;

public class ServerEngineThread extends EngineThread {

	private @NonNull ServerGame game;
	private List<GameSession> sessions = new ArrayList<>();
	private WorldUpdate tmpDeltaWorldUpdate = new WorldUpdate();
	private WorldUpdate tmpFullWorldUpdate = new WorldUpdate();
	private EntityCollection tmpRemoveEntityCollection = new EntityCollection();

	public ServerEngineThread(ServerGame game) {
		super("Main Server Thread");
		this.game = game;
		setWarnLoadTrigger(0.8);
	}

	public synchronized void add(GameSession worldSession) {
		sessions.add(worldSession);
	}

	@Override
	public synchronized void update(double deltaTimeMillis) {
		game.consumeReceivedObjects();
		sessions.forEach(gs -> gs.getWorld().update(deltaTimeMillis));
		sessions.forEach(gs -> {
			if (gs.getWorld().getTime().getTickCount() % 2 == 0) {
				// Send data every 2 frames only
				return;
			}
			sendWorldData(gs);
			gs.clear();
			gs.getWorld().getEntityPool().foreach(entity -> {
				entity.setStateChanged(false);
				if (entity.getChunk() != null) {
					entity.setPreviousUpdateChunkPosition(entity.getChunk().getPosition());
				}
			});
		});
	}

	private void sendWorldData(GameSession gs) {
		PlayerDead[] playerDeads = gs.extractPlayerDeads();
		gs.foreachPlayers(session -> {
			PlayerConnection connection = session.getConnection();
			PlayerEntity playerEntity = connection.getPlayerEntity();
			if (connection.isGameReady() && playerEntity != null) {
				tmpFullWorldUpdate.clear();
				sendWorldUpdate(gs, session, connection);
				if (connection.isInventoryChanged()) {
					connection.setInventoryChanged(false);
					connection.sendTCP(playerEntity.getInventory());
				}
				if (playerDeads != null) {
					connection.sendTCP(playerDeads);
				}
				game.notifyNetworkListeners(l -> l.sent(tmpDeltaWorldUpdate));
			}
			session.clearPositionChanges();
		});

	}

	private void sendWorldUpdate(GameSession gs, PlayerSession session, PlayerConnection connection) {
		if (!connection.isConnected()) {
			return;
		}
		if (ClientAckManager.getInstance().check(connection) && !connection.isRequestedFullUpdate()) {
			prepareDeltaUpdate(gs, session);
			if (!tmpDeltaWorldUpdate.isEmpty()) {
				int length = connection.sendUDP(tmpDeltaWorldUpdate);
				// Log.info("delta update sent to " + connection + " ( entity :
				// " +
				// tmpDeltaWorldUpdate.getEntityUpdateByteBuffer().position() +
				// " total : " + length + ", ping : " + connection.getPing()
				// + ", multiplier : " + connection.getAckThresholdMultiplier()
				// + ")");
			}
		} else {
			if (connection.isRequestedFullUpdate()) {
				prepareFullUpdateForRefresh(session.getConnection().getPlayerEntity());
				connection.setRequestedFullUpdate(false);
			} else {
				prepareFullUpdate(session.getConnection().getPlayerEntity());

			}
			int length = connection.sendUDP(tmpFullWorldUpdate);
			Log.info("full update sent to " + connection + " ( entity : " + tmpFullWorldUpdate.getEntityUpdateByteBuffer().position() + " total : " + length + ", ping : " + connection.getPing()
					+ ", multiplier : " + connection.getAckThresholdMultiplier() + ")");
		}
	}

	private void prepareDeltaUpdate(GameSession gs, PlayerSession session) {
		tmpDeltaWorldUpdate.getCompressedChunks().clear();
		tmpDeltaWorldUpdate.getStructureUpdates().clear();
		session.extractStructureUpdatesToSend(tmpDeltaWorldUpdate.getStructureUpdates());
		session.extractChunksToSend(tmpDeltaWorldUpdate.getCompressedChunks());
		PlayerEntity playerEntity = session.getConnection().getPlayerEntity();
		ByteBuffer byteBuffer = tmpDeltaWorldUpdate.getEntityUpdateByteBuffer();
		byteBuffer.position(0);
		writeRemoveEntity(gs, session, byteBuffer);
		byteBuffer.put(EntityGroup.END_MARKER);
		writeEntityUpdate(session, playerEntity, byteBuffer);
		byteBuffer.put(EntityGroup.END_MARKER);
		writeDistantAllyPositions(playerEntity, byteBuffer);
	}

	private void prepareFullUpdate(PlayerEntity player) {
		prepareFullUpdate(player, ClientAckManager.getInstance().getCompressedChunks(), ClientAckManager.getInstance().getStructureUpdates());
	}

	private void prepareFullUpdateForRefresh(PlayerEntity player) {
		List<CompressedChunk> compressedChunks = new ArrayList<>();
		player.foreachChunkInView(c -> compressedChunks.add(c.getCompressed()));
		prepareFullUpdate(player, compressedChunks, Collections.emptyList());
	}

	private void prepareFullUpdate(PlayerEntity player, List<CompressedChunk> compressedChunks, List<StructureUpdate> structureUpdates) {
		tmpFullWorldUpdate.getCompressedChunks().clear();
		tmpFullWorldUpdate.getCompressedChunks().addAll(compressedChunks);
		tmpFullWorldUpdate.getStructureUpdates().clear();
		tmpFullWorldUpdate.getStructureUpdates().addAll(structureUpdates);
		tmpFullWorldUpdate.getEntityUpdateByteBuffer().position(0);
		ByteBuffer byteBuffer = tmpFullWorldUpdate.getEntityUpdateByteBuffer();
		byteBuffer.put(EntityGroup.REMOVE_ALL_MARKER);
		byteBuffer.put(EntityGroup.END_MARKER);
		player.foreachChunkInView(chunk -> chunk.getEntities().writeUpdate(byteBuffer, true, player.getChunkVision()));
		byteBuffer.put(EntityGroup.END_MARKER);
		writeDistantAllyPositions(player, byteBuffer);
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
