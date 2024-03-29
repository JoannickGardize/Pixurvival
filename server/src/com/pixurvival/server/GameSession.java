package com.pixurvival.server;

import com.pixurvival.core.EndGameData;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.WorldListener;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.chat.ChatListener;
import com.pixurvival.core.contentPack.ContentPackIdentifier;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPoolListener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.PlayerMapEventListener;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.update.AddStructureUpdate;
import com.pixurvival.core.map.chunk.update.RemoveStructureUpdate;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.message.*;
import com.pixurvival.core.team.Team;
import com.pixurvival.server.lobby.LobbySession;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Consumer;

public class GameSession implements TiledMapListener, PlayerMapEventListener, EntityPoolListener, ChatListener, WorldListener, ServerGameListener {

    private @Getter World world;
    private @Getter List<PlayerGameSession> players = new ArrayList<>();
    private Map<Long, PlayerGameSession> sessionsByOriginalPlayers = new HashMap<>();
    private @Getter Map<Long, Set<PlayerGameSession>> sessionsByEntities = new HashMap<>();
    private @Getter Map<ChunkPosition, List<Entity>> removedEntities = new HashMap<>();
    private @Getter Map<ChunkPosition, List<Entity>> chunkChangedEntities = new HashMap<>();
    private List<PlayerDead> playerDeadList = new ArrayList<>();
    private List<PlayerRespawn> playerRespawnList = new ArrayList<>();
    private @Setter TeamComposition[] teamCompositions;
    private List<PlayerGameSession> tmpPlayerSessions = new ArrayList<>();
    private @Getter
    @Setter long previousNetworkReportTime;
    // private @Getter NetworkStatisticsReporter networkReporter = new
    // NetworkStatisticsReporter();
    private @Getter boolean ended = false;

    public GameSession(World world) {
        this.world = world;
        world.getMap().addListener(this);
        world.getMap().addPlayerMapEventListener(this);
        world.getEntityPool().addListener(this);
        world.getChatManager().addListener(this);
        world.addListener(this);
    }

    public void clear() {
        removedEntities.clear();
        chunkChangedEntities.clear();
        playerDeadList.clear();
    }

    public PlayerGameSession createPlayerSession(PlayerConnection player, PlayerEntity playerEntity) {
        PlayerGameSession playerSession = new PlayerGameSession(player, playerEntity);
        players.add(playerSession);
        getSessionsForPlayerEntity(playerEntity).add(playerSession);
        sessionsByOriginalPlayers.put(playerEntity.getId(), playerSession);
        // playerSession.setNetworkListener(networkReporter);
        playerEntity.getInventory().addListener(playerSession);
        playerEntity.addItemCraftDiscoveryListener(playerSession);
        player.addPlayerConnectionMessageListeners(playerSession);
        return playerSession;
    }

    public void foreachPlayers(Consumer<PlayerGameSession> action) {
        players.forEach(action);
    }

    @Override
    public void chunkLoaded(Chunk chunk) {
        for (PlayerGameSession playerSession : players) {
            if (playerSession.isMissingAndRemove(chunk.getPosition())
                    || chunk.getPosition().insideSquare(playerSession.getPlayerEntity().getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
                playerSession.addChunkIfNotKnown(chunk);
            }
        }
    }

    @Override
    public void structureChanged(StructureEntity mapStructure, StructureUpdate structureUpdate) {
        addStructureUpdate(mapStructure, structureUpdate);
    }

    private void addChunk(PlayerGameSession session, ChunkPosition position, Chunk chunk) {
        if (chunk == null) {
            session.addMissingChunk(position);
        } else {
            session.addChunkIfNotKnown(chunk);
        }
    }

    @Override
    public void structureAdded(StructureEntity mapStructure) {
        StructureUpdate structureUpdate = new AddStructureUpdate(mapStructure.getTileX(), mapStructure.getTileY(), mapStructure.getId(), mapStructure.getDefinition().getId(),
                mapStructure.getCreationTime());
        addStructureUpdate(mapStructure, structureUpdate);
    }

    @Override
    public void structureRemoved(StructureEntity mapStructure) {
        StructureUpdate structureUpdate = new RemoveStructureUpdate(mapStructure.getTileX(), mapStructure.getTileY(), mapStructure.getId());
        addStructureUpdate(mapStructure, structureUpdate);
    }

    @Override
    public void chunkUnloaded(Chunk chunk) {
        // I f*cking don't care of this event.
    }

    private void addStructureUpdate(StructureEntity mapStructure, StructureUpdate structureUpdate) {
        for (PlayerGameSession player : players) {
            ChunkPosition chunkPosition = mapStructure.getChunk().getPosition();
            if (chunkPosition.insideSquare(player.getPlayerEntity().getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
                player.addStructureUpdate(structureUpdate);
            } else {
                player.invalidateChunk(chunkPosition);
            }
        }
    }

    @Override
    public void enterVision(PlayerEntity entity, ChunkPosition position) {
        for (PlayerGameSession playerSession : getSessionsForPlayerEntity(entity)) {
            Chunk chunk = world.getMap().chunkAt(position);
            addChunk(playerSession, position, chunk);
            playerSession.addNewPosition(position);
        }
    }

    @Override
    public void exitVision(PlayerEntity entity, ChunkPosition position) {
        for (PlayerGameSession playerSession : getSessionsForPlayerEntity(entity)) {
            if (playerSession != null) {
                playerSession.addOldPosition(position);
            }
        }
    }

    @Override
    public void entityAdded(Entity e) {
        // THIS ONE TOO
    }

    @Override
    public void entityRemoved(Entity e) {
        if (e.getChunk() != null) {
            removedEntities.computeIfAbsent(e.getChunk().getPosition(), position -> new ArrayList<>()).add(e);
        }
    }

    public PlayerDead[] extractPlayerDeads() {
        if (playerDeadList.isEmpty()) {
            return null;
        } else {
            PlayerDead[] result = playerDeadList.toArray(new PlayerDead[playerDeadList.size()]);
            playerDeadList.clear();
            return result;
        }
    }

    public PlayerRespawn[] extractPlayerRespawns() {
        if (playerRespawnList.isEmpty()) {
            return null;
        } else {
            PlayerRespawn[] result = playerRespawnList.toArray(new PlayerRespawn[playerRespawnList.size()]);
            playerRespawnList.clear();
            return result;
        }
    }

    private void spectateBestTarget(PlayerGameSession playerSession) {
        PlayerEntity deadPlayer = playerSession.getPlayerEntity();
        Team team = deadPlayer.getTeam();
        PlayerEntity spectatedPlayer = null;
        if (!team.getAliveMembers().isEmpty()) {
            spectatedPlayer = team.getAliveMembers().iterator().next();

        } else {
            Collection<Entity> collection = deadPlayer.getWorld().getEntityPool().get(EntityGroup.PLAYER);
            if (!collection.isEmpty()) {
                spectatedPlayer = (PlayerEntity) collection.iterator().next();
            }
        }
        if (spectatedPlayer != null) {
            setSessionFocusOn(playerSession, spectatedPlayer);
            playerSession.getConnection().sendTCP(new Spectate(spectatedPlayer));
        }
    }

    private void setSessionFocusOn(PlayerGameSession playerSession, PlayerEntity focusEntiy) {
        getSessionsForPlayerEntity(playerSession.getPlayerEntity()).remove(playerSession);
        getSessionsForPlayerEntity(focusEntiy).add(playerSession);
        playerSession.setPlayerEntity(focusEntiy);
        playerSession.setRequestedFullUpdate(true);
    }

    @Override
    public void entityEnterChunk(ChunkPosition previousPosition, Entity e) {
        if (previousPosition != null) {
            chunkChangedEntities.computeIfAbsent(previousPosition, position -> new ArrayList<>()).add(e);
        }
    }

    @Override
    public void received(ChatEntry chatEntry) {
        players.forEach(p -> p.getConnection().sendTCP(chatEntry));
    }

    @Override
    public void gameEnded(EndGameData data) {
        players.forEach(p -> {
            p.getConnection().sendTCP(data);
            p.getConnection().removePlayerConnectionMessageListeners(p);
        });
        ended = true;
    }

    @Override
    public void playerRejoined(PlayerConnection connection) {
        for (PlayerGameSession ps : players) {
            if (!ps.getConnection().isConnected() && ps.getConnection().toString().equals(connection.toString())) {
                ps.getKnownPositions().clear();
                ps.clearChunkAndStructureUpdates();
                ps.clearPositionChanges();
                PlayerEntity playerEntity = ps.getPlayerEntity();
                if (playerEntity.getLastPlayerMovementRequest() != null) {
                    playerEntity.getLastPlayerMovementRequest().setId(-1);
                }
                playerEntity.foreachChunkInView(c -> ps.addNewPosition(c.getPosition()));
                CreateWorld createWorld = new CreateWorld();
                createWorld.setId(world.getId());
                createWorld.setContentPackIdentifier(new ContentPackIdentifier(world.getContentPack().getIdentifier()));
                createWorld.setGameModeId(world.getGameMode().getId());
                createWorld.setTeamCompositions(teamCompositions);
                createWorld.setMyPlayerId(playerEntity.getId());
                createWorld.setMySpawnCenter(playerEntity.getSpawnPosition());
                createWorld.setMyOriginalPlayerId(ps.getOriginalPlayer().getId());
                createWorld.setMyTeamId(playerEntity.getTeam().getId());
                createWorld.setMyPosition(playerEntity.getPosition());
                createWorld.setInventory(playerEntity.getInventory());
                createWorld.setPlayerDeadIds(playerDeadIds(playerEntity.getWorld()));
                createWorld.setSpectator(ps.isSpectator());
                createWorld.setWorldSpawnCenter(world.getSpawnCenter());
                createWorld.setDiscoveredItemCrafts(playerEntity.getItemCraftDiscovery().getDiscovereditemCraftIds());
                ps.setConnection(connection);
                ps.setRequestedFullUpdate(true);
                ps.setReconnecting(true);
                connection.addPlayerConnectionMessageListeners(ps);
                ps.setGameReady(false);
                ps.resetNetworkData();
                connection.sendTCP(createWorld);
                break;
            }
        }
    }

    private long[] playerDeadIds(World world) {
        List<Long> list = new ArrayList<>();
        world.getPlayerEntities().values().forEach(p -> {
            if (!p.isAlive()) {
                list.add(p.getId());
            }
        });
        long[] result = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    private Collection<PlayerGameSession> getSessionsForPlayerEntity(Entity playerEntity) {
        return sessionsByEntities.computeIfAbsent(playerEntity.getId(), id -> new HashSet<>());
    }

    public PlayerGameSession sessionOfPlayerId(long id) {
        return sessionsByOriginalPlayers.get(id);
    }

    @Override
    public void sneakyEntityRemoved(Entity e) {
    }

    @Override
    public void playerDied(PlayerEntity player) {
        playerDeadList.add(new PlayerDead(player.getId(), player.getRespawnTime()));
        tmpPlayerSessions.clear();
        tmpPlayerSessions.addAll(getSessionsForPlayerEntity(player));
        // Iterate over a copy of the collection because it could be
        // modified inside the
        // loop
        for (PlayerGameSession playerSession : tmpPlayerSessions) {
            playerSession.setSpectator(true);
            playerSession.setInventoryChanged(true);
            spectateBestTarget(playerSession);
        }
    }

    @Override
    public void playerRespawned(PlayerEntity player) {
        playerRespawnList.add(new PlayerRespawn(player.getId()));
        PlayerGameSession playerSession = sessionsByOriginalPlayers.get(player.getId());
        playerSession.setSpectator(false);
        playerSession.setInventoryChanged(true);
        setSessionFocusOn(playerSession, player);
        playerSession.getConnection().sendTCP(new Respawn(player));
    }

    @Override
    public void lobbyStarted(LobbySession lobbySession) {
        // TODO Auto-generated method stub

    }

    @Override
    public void gameStarted(GameSession gameSession) {
        // TODO Auto-generated method stub

    }

    @Override
    public void playerLoggedIn(PlayerConnection playerConnection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void disconnected(PlayerConnection playerConnection) {
        // TODO Auto-generated method stub

    }
}
