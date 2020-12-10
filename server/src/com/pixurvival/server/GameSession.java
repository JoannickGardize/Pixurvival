package com.pixurvival.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.PlayerMapEventListener;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.update.AddStructureUpdate;
import com.pixurvival.core.map.chunk.update.RemoveStructureUpdate;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.message.CreateWorld;
import com.pixurvival.core.message.PlayerDead;
import com.pixurvival.core.message.Spectate;
import com.pixurvival.core.message.TeamComposition;
import com.pixurvival.core.team.Team;

import lombok.Getter;
import lombok.Setter;

public class GameSession implements TiledMapListener, PlayerMapEventListener, EntityPoolListener, ChatListener, WorldListener, ServerGameListener {

	private @Getter World world;
	private List<PlayerGameSession> players = new ArrayList<>();
	private Map<Long, Set<PlayerGameSession>> sessionsByEntities = new HashMap<>();
	private @Getter Map<ChunkPosition, List<Entity>> removedEntities = new HashMap<>();
	private @Getter Map<ChunkPosition, List<Entity>> chunkChangedEntities = new HashMap<>();
	private List<PlayerDead> playerDeadList = new ArrayList<>();
	private @Setter TeamComposition[] teamCompositions;
	private List<PlayerGameSession> tmpPlayerSessions = new ArrayList<>();
	private @Getter @Setter long previousNetworkReportTime;
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
		// playerSession.setNetworkListener(networkReporter);
		playerEntity.getInventory().addListener(playerSession);
		player.addPlayerConnectionMessageListeners(playerSession);
		return playerSession;
	}

	public void foreachPlayers(Consumer<PlayerGameSession> action) {
		players.forEach(action);
	}

	@Override
	public void chunkLoaded(Chunk chunk) {
		for (PlayerGameSession playerSession : players) {
			if (playerSession.isMissingAndRemove(chunk.getPosition()) || chunk.getPosition().insideSquare(playerSession.getPlayerEntity().getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
				playerSession.addChunkIfNotKnown(chunk);
			}
		}
	}

	@Override
	public void structureChanged(MapStructure mapStructure, StructureUpdate structureUpdate) {
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
	public void structureAdded(MapStructure mapStructure) {
		StructureUpdate structureUpdate = new AddStructureUpdate(mapStructure.getTileX(), mapStructure.getTileY(), mapStructure.getDefinition().getId(), mapStructure.getCreationTime());
		addStructureUpdate(mapStructure, structureUpdate);
	}

	@Override
	public void structureRemoved(MapStructure mapStructure) {
		StructureUpdate structureUpdate = new RemoveStructureUpdate(mapStructure.getTileX(), mapStructure.getTileY());
		addStructureUpdate(mapStructure, structureUpdate);
	}

	@Override
	public void chunkUnloaded(Chunk chunk) {
		// I f*cking don't care of this event.
	}

	private void addStructureUpdate(MapStructure mapStructure, StructureUpdate structureUpdate) {
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
		// TODO avoid this if with specific events for PlayerEntities
		if (e instanceof PlayerEntity) {
			playerDeadList.add(new PlayerDead(e.getId()));
			tmpPlayerSessions.clear();
			tmpPlayerSessions.addAll(getSessionsForPlayerEntity(e));
			// Iterate over a copy of the collection because it could be
			// modified inside the
			// loop
			for (PlayerGameSession playerSession : tmpPlayerSessions) {
				playerSession.setSpectator(true);
				findBestSpectatorTarget(playerSession);
			}
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

	private void findBestSpectatorTarget(PlayerGameSession playerSession) {
		PlayerEntity previousPlayer = playerSession.getPlayerEntity();
		Team team = previousPlayer.getTeam();
		PlayerEntity spectatedPlayer = null;
		if (!team.getAliveMembers().isEmpty()) {
			spectatedPlayer = team.getAliveMembers().iterator().next();

		} else {
			Collection<Entity> collection = previousPlayer.getWorld().getEntityPool().get(EntityGroup.PLAYER);
			if (!collection.isEmpty()) {
				spectatedPlayer = (PlayerEntity) collection.iterator().next();
			}
		}
		if (spectatedPlayer != null) {
			getSessionsForPlayerEntity(previousPlayer).remove(playerSession);
			getSessionsForPlayerEntity(spectatedPlayer).add(playerSession);
			playerSession.setPlayerEntity(spectatedPlayer);
			playerSession.setRequestedFullUpdate(true);
			playerSession.getConnection().sendTCP(new Spectate(spectatedPlayer));
		}
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
	public void playerLoggedIn(PlayerConnection connection) {
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
				createWorld.setMyTeamId(playerEntity.getTeam().getId());
				createWorld.setMyPosition(playerEntity.getPosition());
				createWorld.setInventory(playerEntity.getInventory());
				createWorld.setPlayerDeadIds(playerDeadIds(playerEntity.getWorld()));
				createWorld.setSpectator(ps.isSpectator());
				ps.setConnection(connection);
				ps.setReconnected(true);
				ps.setRequestedFullUpdate(true);
				connection.addPlayerConnectionMessageListeners(ps);
				ps.setGameReady(false);
				ps.resetNetworkData();
				connection.sendTCP(createWorld);
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

	@Override
	public void sneakyEntityRemoved(Entity e) {
	}
}
