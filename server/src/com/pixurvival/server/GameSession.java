package com.pixurvival.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.Position;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.message.AddStructureUpdate;
import com.pixurvival.core.message.RemoveStructureUpdate;
import com.pixurvival.core.message.StructureUpdate;

import lombok.Getter;

public class GameSession implements TiledMapListener {

	private @Getter World world;
	private Map<Long, PlayerSession> players = new HashMap<>();

	public GameSession(World world) {
		this.world = world;
		world.getMap().addListener(this);
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
			if (playerSession.missThisChunk(chunk.getPosition())
					|| playerSession.getConnection().getPlayerEntity().getChunkPosition().insideSquare(chunk.getPosition(), GameConstants.PLAYER_CHUNK_VIEW_DISTANCE)) {
				playerSession.addChunkIfNotKnown(chunk);
			}
		}
	}

	@Override
	public void structureChanged(MapStructure mapStructure) {
		StructureUpdate structureUpdate = mapStructure.getUpdate();
		addStructureUpdate(mapStructure, structureUpdate);
	}

	@Override
	public void playerChangedChunk(PlayerEntity player) {
		PlayerSession playerSession = players.get(player.getId());
		if (playerSession != null) {
			Position position = player.getChunkPosition();

			for (int x = position.getX() - GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; x <= position.getX() + GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; x++) {
				for (int y = position.getY() - GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; y <= position.getY() + GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; y++) {
					Position chunkPosition = new Position(x, y);
					Chunk chunk = world.getMap().chunkAt(chunkPosition);
					addChunk(playerSession, chunkPosition, chunk);
					chunkPosition = new Position(x, position.getY() + GameConstants.PLAYER_CHUNK_VIEW_DISTANCE);
					chunk = world.getMap().chunkAt(chunkPosition);
					addChunk(playerSession, chunkPosition, chunk);
				}
			}

		}
	}

	private void addChunk(PlayerSession session, Position position, Chunk chunk) {
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
	}

	private void addStructureUpdate(MapStructure mapStructure, StructureUpdate structureUpdate) {
		for (PlayerSession player : players.values()) {
			Position chunkPosition = mapStructure.getChunk().getPosition();
			if (player.getConnection().getPlayerEntity().getChunkPosition().insideSquare(chunkPosition, GameConstants.PLAYER_CHUNK_VIEW_DISTANCE)) {
				player.addStructureUpdate(structureUpdate);
			} else {
				player.invalidateChunk(chunkPosition);
			}
		}
	}

}
