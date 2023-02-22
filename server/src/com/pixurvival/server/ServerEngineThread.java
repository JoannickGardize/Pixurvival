package com.pixurvival.server;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.EngineThread;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.SoundEffect;
import com.pixurvival.core.chat.ChatEntry;
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
import com.pixurvival.core.message.PlayerRespawn;
import com.pixurvival.core.message.WorldUpdate;
import com.pixurvival.core.util.LongSequenceIOHelper;
import com.pixurvival.server.lobby.LobbySession;
import lombok.NonNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerEngineThread extends EngineThread {

    private @NonNull PixurvivalServer game;
    private List<GameSession> gameSessions = new ArrayList<>();
    private List<GameSession> tmpRemovedSessions = new ArrayList<>();
    private WorldUpdate tmpDeltaWorldUpdate = new WorldUpdate();
    private WorldUpdate tmpFullWorldUpdate = new WorldUpdate();
    private EntityCollection tmpRemoveEntityCollection = new EntityCollection();

    private List<String> requestedCommands = new ArrayList<>();

    public ServerEngineThread(PixurvivalServer game) {
        super("Main Server Thread");
        this.game = game;
        setWarnLoadTrigger(0.8f);
    }

    public void requestCommand(String command) {
        synchronized (requestedCommands) {
            requestedCommands.add(command);
        }
    }

    public synchronized void add(GameSession gameSession) {
        gameSession.setPreviousNetworkReportTime(System.currentTimeMillis());
        gameSessions.add(gameSession);
    }

    @Override
    public void update(float deltaTimeMillis) {
        game.consumeReceivedObjects();
        gameSessions.forEach(gs -> {
            synchronized (requestedCommands) {
                while (!requestedCommands.isEmpty()) {
                    gs.getWorld().received(new ChatEntry(gs.getWorld(), requestedCommands.remove(0)));
                }
            }
            if (gs.isEnded()) {
                LobbySession lobbySession = new LobbySession(game);
                gs.foreachPlayers(p -> lobbySession.addPlayer(p.getConnection()));
                game.addLobbySession(lobbySession);
                tmpRemovedSessions.add(gs);
                gs.getWorld().unload();
                return;
            }
            gs.getWorld().update(deltaTimeMillis);
            // long elapsed = System.currentTimeMillis() -
            // gs.getPreviousNetworkReportTime();
            // if (elapsed >= 10_000) {
            // gs.getNetworkReporter().report(elapsed);
            // gs.setPreviousNetworkReportTime(System.currentTimeMillis());
            // }
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
        gameSessions.removeAll(tmpRemovedSessions);
        tmpRemovedSessions.clear();
    }

    private void sendWorldData(GameSession gs) {
        PlayerDead[] playerDeads = gs.extractPlayerDeads();
        PlayerRespawn[] playerRespawns = gs.extractPlayerRespawns();
        gs.getWorld().getTime().setSerializationContextTimeToNow();
        gs.foreachPlayers(session -> {
            PlayerEntity playerEntity = session.getPlayerEntity();
            if (session.isGameReady() && playerEntity != null) {
                sendWorldUpdate(gs, session);
                if (session.isInventoryChanged()) {
                    session.setInventoryChanged(false);
                    session.getConnection().sendTCP(playerEntity.getInventory());
                }
                if (playerDeads != null) {
                    session.getConnection().sendTCP(playerDeads);
                }
                if (playerRespawns != null) {
                    session.getConnection().sendTCP(playerRespawns);
                }
            }
            session.clearPositionChanges();
        });
    }

    private void sendWorldUpdate(GameSession gs, PlayerGameSession session) {
        if (!session.getConnection().isConnected()) {
            return;
        }
        if (ClientAckManager.getInstance().check(session) && !session.isRequestedFullUpdate()) {
            prepareDeltaUpdate(gs, session);
            if (!tmpDeltaWorldUpdate.isEmpty()) {
                /* int length = */
                session.sendUDP(tmpDeltaWorldUpdate);
                // Log.info("delta update sent to " + session.getConnection() + " entity size :
                // " + tmpDeltaWorldUpdate.getEntityUpdateByteBuffer().position() + ", size : "
                // + length);
            }
        } else {
            if (session.isRequestedFullUpdate()) {
                prepareFullUpdateForRefresh(session.getPlayerEntity());
                session.setRequestedFullUpdate(false);
            } else {
                Log.warn("UDP packet loss detected for " + session.getConnection() + ", sending full update.");
                // TODO also add chunks and structure updates of the player
                // session
                prepareFullUpdate(session.getPlayerEntity());
            }
            /* int length = */
            session.sendUDP(tmpFullWorldUpdate);
            // Log.info("full update sent to " + session.getConnection() + " entity size : "
            // + tmpFullWorldUpdate.getEntityUpdateByteBuffer().position() + ", size : " +
            // length);
        }
    }

    private void prepareDeltaUpdate(GameSession gs, PlayerGameSession session) {
        tmpDeltaWorldUpdate.getCompressedChunks().clear();
        tmpDeltaWorldUpdate.getStructureUpdates().clear();
        session.extractStructureUpdatesToSend(tmpDeltaWorldUpdate.getStructureUpdates());
        session.extractChunksToSend(tmpDeltaWorldUpdate.getCompressedChunks());
        PlayerEntity playerEntity = session.getPlayerEntity();
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
        ByteBuffer byteBuffer = tmpFullWorldUpdate.getEntityUpdateByteBuffer();
        byteBuffer.position(0);
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
        int size = Math.min(soundEffects.size(), 10);
        for (int i = 0; i < size; i++) {
            SoundEffect soundEffect = soundEffects.get(i);
            if (player.distanceSquared(soundEffect.getPosition()) <= GameConstants.PLAYER_VIEW_DISTANCE * GameConstants.PLAYER_VIEW_DISTANCE) {
                worldUpdate.getSoundEffects().add(soundEffect);
            }
        }
    }

    private void writeDeltaEntityUpdate(PlayerGameSession session, PlayerEntity playerEntity, ByteBuffer byteBuffer) {
        playerEntity.foreachChunkInView(chunk -> {
            if (session.isNewPosition(chunk.getPosition())) {
                chunk.getEntities().writeFullUpdate(byteBuffer);
            } else {
                chunk.getEntities().writeDeltaUpdate(byteBuffer, playerEntity.getChunkVision());
            }
        });
    }

    private void writeRemoveEntity(GameSession gs, PlayerGameSession session, ByteBuffer byteBuffer) {
        PlayerEntity playerEntity = session.getPlayerEntity();
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

    private void writeDistantAllyPositions(PlayerEntity player, ByteBuffer buffer) {
        LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
        for (Entity ally : player.getTeam().getAliveMembers()) {
            if (ally != player && ally.getChunk() != null && !ally.getChunk().getPosition().insideSquare(player.getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
                idSequence.write(buffer, ally.getId());
                buffer.putFloat(ally.getPosition().getX());
                buffer.putFloat(ally.getPosition().getY());
                buffer.putFloat(ally.getVelocity().getX());
                buffer.putFloat(ally.getVelocity().getY());
            }
        }
        idSequence.reWriteLast(buffer);
    }

    @Override
    protected void frameSkipped() {
        Log.warn("Server frames skipped.");
    }
}
