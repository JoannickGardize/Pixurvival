package com.pixurvival.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.chat.ChatEntry;
import com.pixurvival.core.chat.ChatListener;
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
import com.pixurvival.core.message.PlayerDead;
import com.pixurvival.core.message.Spectate;
import com.pixurvival.core.team.Team;

import lombok.Getter;

public class GameSession implements TiledMapListener, PlayerMapEventListener, EntityPoolListener, ChatListener {

	private @Getter World world;
	private Map<Long, PlayerSession> players = new HashMap<>();
	private Map<Long, SpectatorSession> spectators = new HashMap<>();
	private @Getter Map<ChunkPosition, List<Entity>> removedEntities = new HashMap<>();
	private @Getter Map<ChunkPosition, List<Entity>> chunkChangedEntities = new HashMap<>();
	private List<PlayerDead> playerDeadList = new ArrayList<>();

	public GameSession(World world) {
		this.world = world;
		world.getMap().addListener(this);
		world.getMap().addPlayerMapEventListener(this);
		world.getEntityPool().addListener(this);
		world.getChatManager().addListener(this);
	}

	public void clear() {
		removedEntities.clear();
		chunkChangedEntities.clear();
		playerDeadList.clear();
	}

	public void addPlayer(PlayerConnection player) {
		players.put(player.getPlayerEntity().getId(), new PlayerSession(player));
	}

	public void foreachPlayers(Consumer<PlayerSession> action) {
		players.values().forEach(action);
	}

	@Override
	public void chunkLoaded(Chunk chunk) {
		for (PlayerSession playerSession : players.values()) {
			if (playerSession.isMissingAndRemove(chunk.getPosition())
					|| chunk.getPosition().insideSquare(playerSession.getConnection().getPlayerEntity().getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
				playerSession.addChunkIfNotKnown(chunk);
			}
		}
	}

	@Override
	public void structureChanged(MapStructure mapStructure) {
		StructureUpdate structureUpdate = mapStructure.getUpdate();
		addStructureUpdate(mapStructure, structureUpdate);
	}

	private void addChunk(PlayerSession session, ChunkPosition position, Chunk chunk) {
		if (chunk == null) {
			session.addMissingChunk(position);
		} else {
			session.addChunkIfNotKnown(chunk);
		}
	}

	@Override
	public void structureAdded(MapStructure mapStructure) {
		StructureUpdate structureUpdate = new AddStructureUpdate(mapStructure.getTileX(), mapStructure.getTileY(), mapStructure.getDefinition().getId());
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
		for (PlayerSession player : players.values()) {
			ChunkPosition chunkPosition = mapStructure.getChunk().getPosition();
			if (chunkPosition.insideSquare(player.getConnection().getPlayerEntity().getPosition(), GameConstants.PLAYER_VIEW_DISTANCE)) {
				player.addStructureUpdate(structureUpdate);
			} else {
				player.invalidateChunk(chunkPosition);
			}
		}
	}

	@Override
	public void enterVision(PlayerEntity entity, ChunkPosition position) {
		PlayerSession playerSession = players.get(entity.getId());
		if (playerSession != null) {
			Chunk chunk = world.getMap().chunkAt(position);
			addChunk(playerSession, position, chunk);
			playerSession.addNewPosition(position);
		}
	}

	@Override
	public void exitVision(PlayerEntity entity, ChunkPosition position) {
		PlayerSession playerSession = players.get(entity.getId());
		if (playerSession != null) {
			playerSession.addOldPosition(position);
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
			PlayerSession playerSession = players.remove(e.getId());
			if (!players.isEmpty()) {
				SpectatorSession spectatorSession = new SpectatorSession();
				spectatorSession.setConnection(playerSession.getConnection());
				spectators.put(spectatorSession.getConnection().getPlayerEntity().getId(), spectatorSession);
				findBestSpectatorTarget(spectatorSession);
			}
			playerSession.getSpectators().values().forEach(this::findBestSpectatorTarget);
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

	private void findBestSpectatorTarget(SpectatorSession spec) {
		unSpectate(spec);
		Team team = spec.getConnection().getPlayerEntity().getTeam();
		PlayerEntity spectatedPlayer = null;
		if (!team.getAliveMembers().isEmpty()) {
			spectatedPlayer = team.getAliveMembers().iterator().next();
		} else {
			Collection<Entity> collection = spec.getConnection().getPlayerEntity().getWorld().getEntityPool().get(EntityGroup.PLAYER);
			if (!collection.isEmpty()) {
				spectatedPlayer = (PlayerEntity) collection.iterator().next();
			}
		}
		if (spectatedPlayer != null) {
			PlayerSession playerSession = players.get(spectatedPlayer.getId());
			if (playerSession != null) {
				spectate(spec, playerSession);
			}
		}
	}

	private void spectate(SpectatorSession spec, PlayerSession player) {
		spec.setSpectatedPlayer(player);
		player.getSpectators().put(spec.getConnection().getPlayerEntity().getId(), spec);
		spec.setNewlySpectating(true);
		spec.getConnection().sendTCP(new Spectate(player.getConnection().getPlayerEntity()));
	}

	private void unSpectate(SpectatorSession spec) {
		if (spec.getSpectatedPlayer() != null) {
			PlayerSession playerSession = players.get(spec.getSpectatedPlayer().getConnection().getPlayerEntity().getId());
			if (playerSession != null) {
				playerSession.getSpectators().remove(spec.getConnection().getPlayerEntity().getId());
			}
			spec.setSpectatedPlayer(null);
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
		players.values().forEach(p -> p.getConnection().sendTCP(chatEntry));
	}
}
