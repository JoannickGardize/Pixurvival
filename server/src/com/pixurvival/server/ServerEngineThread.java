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
import com.pixurvival.server.ClientAckManager.CheckResult;

import lombok.NonNull;

public class ServerEngineThread extends EngineThread {

	private @NonNull ServerGame game;
	private List<GameSession> sessions = new ArrayList<>();
	private WorldUpdate tmpDeltaWorldUpdate = new WorldUpdate();
	private WorldUpdate tmpFullWorldUpdate = new WorldUpdate();
	private List<SpectatorSession> tmpNewSpectators = new ArrayList<>();
	private EntityCollection tmpRemoveEntityCollection = new EntityCollection();
	private boolean tmpDeltaWorldUpdateReady = false;
	private boolean tmpFullWorldUpdateReady = false;
	private boolean tmpFullWorldUpdateIncludesChunks = false;

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
			tmpDeltaWorldUpdate.setUpdateId(tmpDeltaWorldUpdate.getUpdateId() + 1);
			tmpFullWorldUpdate.setUpdateId(tmpDeltaWorldUpdate.getUpdateId());
			sendWorldData(gs);
			gs.clear();

			gs.getWorld().getEntityPool().foreach(entity -> entity.setStateChanged(false));
		});
		sessions.forEach(gs -> gs.getWorld().getEntityPool().foreach(entity -> {
			if (entity.getChunk() != null) {
				entity.setPreviousUpdateChunkPosition(entity.getChunk().getPosition());
			}
		}));
	}

	private void sendWorldData(GameSession gs) {
		PlayerDead[] playerDeads = gs.extractPlayerDeads();
		gs.foreachPlayers(session -> {
			PlayerConnection connection = session.getConnection();
			PlayerEntity playerEntity = connection.getPlayerEntity();
			if (connection.isGameReady() && playerEntity != null) {
				tmpDeltaWorldUpdateReady = false;
				tmpFullWorldUpdateReady = false;
				tmpFullWorldUpdateIncludesChunks = false;
				tmpFullWorldUpdate.clear();
				sendWorldUpdate(gs, session, connection);
				if (connection.isInventoryChanged()) {
					connection.setInventoryChanged(false);
					connection.sendTCP(playerEntity.getInventory());
				}
				if (playerDeads != null) {
					connection.sendTCP(playerDeads);
				}
				tmpNewSpectators.clear();
				session.getSpectators().values().forEach(spec -> {
					if (spec.isNewlySpectating()) {
						tmpNewSpectators.add(spec);
					} else {
						sendWorldUpdate(gs, session, spec.getConnection());
					}
					if (connection.isInventoryChanged()) {
						spec.getConnection().sendTCP(playerEntity.getInventory());
					}
					if (playerDeads != null) {
						spec.getConnection().sendTCP(playerDeads);
					}
				});
				sendFullUpdateForNewSpectators(playerEntity);
				game.notifyNetworkListeners(l -> l.sent(tmpDeltaWorldUpdate));
			}
			session.clearPositionChanges();
		});

	}

	private void sendWorldUpdate(GameSession gs, PlayerSession session, PlayerConnection connection) {
		if (!connection.isConnected()) {
			return;
		}
		CheckResult checkResult;
		if ((checkResult = ClientAckManager.getInstance().check(connection)) == CheckResult.OK && !connection.isRequestedFullUpdate()) {
			prepareDeltaUpdate(gs, session);
			if (!tmpDeltaWorldUpdate.isEmpty()) {
				int length = connection.sendUDP(tmpDeltaWorldUpdate);
				// Log.info("delta update sent to " + connection + " ( entity :
				// " +
				// tmpDeltaWorldUpdate.getEntityUpdateByteBuffer().position() +
				// " total : " + length + ")");
			}
		} else {
			prepareFullUpdate(session.getConnection().getPlayerEntity(), checkResult == CheckResult.REQUIRE_FULL || connection.isRequestedFullUpdate());
			connection.setRequestedFullUpdate(false);
			int length = connection.sendUDP(tmpFullWorldUpdate);
			Log.info("full update sent to " + connection + " ( entity : " + tmpFullWorldUpdate.getEntityUpdateByteBuffer().position() + " total : " + length + ")");
		}
	}

	private void prepareDeltaUpdate(GameSession gs, PlayerSession session) {
		if (tmpDeltaWorldUpdateReady) {
			return;
		}
		tmpDeltaWorldUpdateReady = true;
		PlayerEntity playerEntity = session.getConnection().getPlayerEntity();
		tmpDeltaWorldUpdate.clear();
		ByteBuffer byteBuffer = tmpDeltaWorldUpdate.getEntityUpdateByteBuffer();
		writeRemoveEntity(gs, session, byteBuffer);
		byteBuffer.put(EntityGroup.END_MARKER);
		writeEntityUpdate(session, playerEntity, byteBuffer);
		byteBuffer.put(EntityGroup.END_MARKER);
		writeDistantAllyPositions(playerEntity, byteBuffer);
		session.extractStructureUpdatesToSend(tmpDeltaWorldUpdate.getStructureUpdates());
		session.extractChunksToSend(tmpDeltaWorldUpdate.getCompressedChunks());
	}

	private void prepareFullUpdate(PlayerEntity player, boolean includeChunks) {
		if (tmpFullWorldUpdateIncludesChunks != includeChunks) {
			tmpFullWorldUpdate.getCompressedChunks().clear();
			tmpFullWorldUpdateIncludesChunks = includeChunks;
			if (includeChunks) {
				player.foreachChunkInView(chunk -> tmpFullWorldUpdate.getCompressedChunks().add(chunk.getCompressed()));
			}
		}
		if (tmpFullWorldUpdateReady) {
			return;
		}
		tmpFullWorldUpdate.getStructureUpdates().clear();
		tmpFullWorldUpdate.getEntityUpdateByteBuffer().position(0);
		tmpFullWorldUpdateReady = true;
		ByteBuffer byteBuffer = tmpFullWorldUpdate.getEntityUpdateByteBuffer();
		byteBuffer.put(EntityGroup.REMOVE_ALL_MARKER);
		byteBuffer.put(EntityGroup.END_MARKER);
		player.foreachChunkInView(chunk -> chunk.getEntities().writeUpdate(byteBuffer, true, player.getChunkVision()));
		byteBuffer.put(EntityGroup.END_MARKER);
		writeDistantAllyPositions(player, byteBuffer);
	}

	private void sendFullUpdateForNewSpectators(PlayerEntity viewedPlayer) {
		if (tmpNewSpectators.isEmpty()) {
			return;
		}
		prepareFullUpdate(viewedPlayer, true);
		tmpNewSpectators.forEach(spec -> {
			spec.getConnection().sendUDP(tmpDeltaWorldUpdate);
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
