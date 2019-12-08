package com.pixurvival.server;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EngineThread;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityCollection;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.ability.CooldownAbilityData;
import com.pixurvival.core.livingEntity.ability.EquipmentAbilityType;
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
		setWarnLoadTrigger(0.8f);
	}

	public synchronized void add(GameSession worldSession) {
		worldSession.setPreviousNetworkReportTime(System.currentTimeMillis());
		sessions.add(worldSession);
	}

	@Override
	public synchronized void update(float deltaTimeMillis) {
		game.consumeReceivedObjects();
		sessions.forEach(gs -> {
			gs.getWorld().update(deltaTimeMillis);
			long elapsed = System.currentTimeMillis() - gs.getPreviousNetworkReportTime();
			if (elapsed >= 10_000) {
				gs.getNetworkReporter().report(elapsed);
				gs.setPreviousNetworkReportTime(System.currentTimeMillis());
			}
			if (gs.getWorld().getTime().getTickCount() % 2 == 0) {
				// Send data every 2 frames only
				return;
			}
			sendWorldData(gs);
			gs.clear();
			gs.getWorld().getEntityPool().get(EntityGroup.PLAYER).forEach(p -> ((PlayerEntity) p).getSoundEffectsToConsume().clear());
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
				sendWorldUpdate(gs, session, connection);
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

	private void sendWorldUpdate(GameSession gs, PlayerSession session, PlayerConnection connection) {
		if (!connection.isConnected()) {
			return;
		}
		if (ClientAckManager.getInstance().check(connection) && !connection.isRequestedFullUpdate()) {
			prepareDeltaUpdate(gs, session);
			if (!tmpDeltaWorldUpdate.isEmpty()) {
				int size = connection.sendUDP(tmpDeltaWorldUpdate);
			}
		} else {
			if (connection.isRequestedFullUpdate()) {
				prepareFullUpdateForRefresh(session.getConnection().getPlayerEntity());
				connection.setRequestedFullUpdate(false);
			} else {
				// TODO also add chunks and structure updates of the player
				// session
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
		writeDeltaEntityUpdate(session, playerEntity, byteBuffer);
		byteBuffer.put(EntityGroup.END_MARKER);
		prepareCommonUpdatePart(tmpDeltaWorldUpdate, playerEntity, playerEntity.getSoundEffectsToConsume());

	}

	private void prepareFullUpdate(PlayerEntity player) {
		List<SoundEffect> resendSoundEffects = ClientAckManager.getInstance().getSoundEffects();
		List<SoundEffect> newSoundEffects = player.getSoundEffectsToConsume();
		List<SoundEffect> totalSoundEffects = new ArrayList<>(newSoundEffects.size() + resendSoundEffects.size());
		totalSoundEffects.addAll(newSoundEffects);
		totalSoundEffects.addAll(resendSoundEffects);
		prepareFullUpdate(player, ClientAckManager.getInstance().getCompressedChunks(), ClientAckManager.getInstance().getStructureUpdates(), totalSoundEffects);
	}

	private void prepareFullUpdateForRefresh(PlayerEntity player) {
		List<CompressedChunk> compressedChunks = new ArrayList<>();
		player.foreachChunkInView(c -> compressedChunks.add(c.getCompressed()));
		prepareFullUpdate(player, compressedChunks, Collections.emptyList(), player.getSoundEffectsToConsume());
	}

	private void prepareFullUpdate(PlayerEntity player, List<CompressedChunk> compressedChunks, List<StructureUpdate> structureUpdates, List<SoundEffect> soundEffects) {
		tmpFullWorldUpdate.getCompressedChunks().clear();
		tmpFullWorldUpdate.getCompressedChunks().addAll(compressedChunks);
		tmpFullWorldUpdate.getStructureUpdates().clear();
		tmpFullWorldUpdate.getStructureUpdates().addAll(structureUpdates);
		tmpFullWorldUpdate.getEntityUpdateByteBuffer().position(0);
		ByteBuffer byteBuffer = tmpFullWorldUpdate.getEntityUpdateByteBuffer();
		byteBuffer.put(EntityGroup.REMOVE_ALL_MARKER);
		byteBuffer.put(EntityGroup.END_MARKER);
		player.foreachChunkInView(chunk -> chunk.getEntities().writeFullUpdate(byteBuffer));
		byteBuffer.put(EntityGroup.END_MARKER);
		prepareCommonUpdatePart(tmpFullWorldUpdate, player, soundEffects);

	}

	private void prepareCommonUpdatePart(WorldUpdate worldUpdate, PlayerEntity player, List<SoundEffect> soundEffects) {
		writeDistantAllyPositions(player, worldUpdate.getEntityUpdateByteBuffer());
		worldUpdate.setTime(player.getWorld().getTime().getTimeMillis());
		worldUpdate.getReadyCooldowns()[0] = ((CooldownAbilityData) player.getAbilityData(EquipmentAbilityType.WEAPON_BASE.getAbilityId())).getReadyTimeMillis();
		worldUpdate.getReadyCooldowns()[1] = ((CooldownAbilityData) player.getAbilityData(EquipmentAbilityType.WEAPON_SPECIAL.getAbilityId())).getReadyTimeMillis();
		worldUpdate.getReadyCooldowns()[2] = ((CooldownAbilityData) player.getAbilityData(EquipmentAbilityType.ACCESSORY1_SPECIAL.getAbilityId())).getReadyTimeMillis();
		worldUpdate.getReadyCooldowns()[3] = ((CooldownAbilityData) player.getAbilityData(EquipmentAbilityType.ACCESSORY2_SPECIAL.getAbilityId())).getReadyTimeMillis();
		worldUpdate.setLastPlayerMovementRequest(player.getLastPlayerMovementRequest());
		worldUpdate.getSoundEffects().clear();
		for (SoundEffect soundEffect : soundEffects) {
			if (player.distanceSquared(soundEffect.getPosition()) <= GameConstants.PLAYER_VIEW_DISTANCE * GameConstants.PLAYER_VIEW_DISTANCE) {
				worldUpdate.getSoundEffects().addAll(soundEffects);
			}
		}
	}

	private void writeDeltaEntityUpdate(PlayerSession session, PlayerEntity playerEntity, ByteBuffer byteBuffer) {
		playerEntity.foreachChunkInView(chunk -> {
			if (session.isNewPosition(chunk.getPosition())) {
				chunk.getEntities().writeFullUpdate(byteBuffer);
			} else {
				chunk.getEntities().writeDeltaUpdate(byteBuffer, playerEntity.getChunkVision());
			}
		});
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
				byteBuffer.putFloat(ally.getPosition().getX());
				byteBuffer.putFloat(ally.getPosition().getY());
				byteBuffer.putFloat(ally.getVelocity().getX());
				byteBuffer.putFloat(ally.getVelocity().getY());
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
