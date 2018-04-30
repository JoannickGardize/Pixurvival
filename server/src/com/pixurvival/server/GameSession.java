package com.pixurvival.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.Position;
import com.pixurvival.core.map.TiledMapListener;
import com.pixurvival.core.message.HarvestableStructureUpdate;

import lombok.Getter;

public class GameSession implements TiledMapListener {

	private @Getter World world;
	private List<HarvestableStructureUpdate> structureUpdates = new ArrayList<>();
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
			if (playerSession.missThisChunk(chunk.getPosition()) || playerSession.getConnection().getPlayerEntity()
					.getChunkPosition().insideSquare(chunk.getPosition(), GameConstants.PLAYER_CHUNK_VIEW_DISTANCE)) {
				playerSession.addChunkIfNotKnown(chunk);
			}
		}
	}

	@Override
	public void structureChanged(MapStructure mapStructure) {
		if (mapStructure instanceof HarvestableStructure) {
			HarvestableStructure hs = (HarvestableStructure) mapStructure;
			structureUpdates.add(new HarvestableStructureUpdate(hs.getTileX(), hs.getTileY(), hs.isHarvested()));
		}
	}

	@Override
	public void playerChangedChunk(PlayerEntity player) {
		PlayerSession playerSession = players.get(player.getId());
		if (playerSession != null) {
			Position position = player.getChunkPosition();

			// for (int x = position.getX() - GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; x <=
			// position.getX()
			// + GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; x++) {
			// Position chunkPosition = new Position(x, position.getY() -
			// GameConstants.PLAYER_CHUNK_VIEW_DISTANCE);
			// Chunk chunk = world.getMap().chunkAt(chunkPosition);
			// addChunk(playerSession, chunkPosition, chunk);
			// chunkPosition = new Position(x, position.getY() +
			// GameConstants.PLAYER_CHUNK_VIEW_DISTANCE);
			// chunk = world.getMap().chunkAt(chunkPosition);
			// addChunk(playerSession, chunkPosition, chunk);
			// }
			// for (int y = position.getY() - GameConstants.PLAYER_CHUNK_VIEW_DISTANCE + 1;
			// y <= position.getY()
			// + GameConstants.PLAYER_CHUNK_VIEW_DISTANCE - 1; y++) {
			// Position chunkPosition = new Position(position.getX() -
			// GameConstants.PLAYER_CHUNK_VIEW_DISTANCE, y);
			// Chunk chunk = world.getMap().chunkAt(chunkPosition);
			// addChunk(playerSession, chunkPosition, chunk);
			// chunkPosition = new Position(position.getX() +
			// GameConstants.PLAYER_CHUNK_VIEW_DISTANCE, y);
			// chunk = world.getMap().chunkAt(chunkPosition);
			// addChunk(playerSession, chunkPosition, chunk);
			// }

			for (int x = position.getX() - GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; x <= position.getX()
					+ GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; x++) {
				for (int y = position.getY() - GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; y <= position.getY()
						+ GameConstants.PLAYER_CHUNK_VIEW_DISTANCE; y++) {
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

	public HarvestableStructureUpdate[] consumeStructureUpdates() {
		HarvestableStructureUpdate[] result = structureUpdates
				.toArray(new HarvestableStructureUpdate[structureUpdates.size()]);
		structureUpdates.clear();
		return result;
	}

	@Override
	public void structureAdded(MapStructure mapStructure) {
		// TODO Auto-generated method stub

	}

	@Override
	public void structureRemoved(MapStructure mapStructure) {
		// TODO Auto-generated method stub

	}

	@Override
	public void chunkUnloaded(Chunk chunk) {
		// TODO Auto-generated method stub

	}

}
