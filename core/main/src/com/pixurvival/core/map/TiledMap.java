package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.message.StructureUpdate;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;

public class TiledMap {

	private List<TiledMapListener> listeners = new ArrayList<>();
	private List<Chunk> newChunks = new ArrayList<>();
	private List<Chunk> toRemoveChunks = new ArrayList<>();

	@Getter
	private World world;

	private MapTile outsideTile;
	private int chunkDistance;
	@Getter
	private MapTile[] mapTilesById;

	@Getter
	private TiledMapLimits limits = new TiledMapLimits();

	private Map<ChunkPosition, Chunk> chunks = new HashMap<>();

	private Map<ChunkPosition, List<StructureUpdate>> waitingStructureUpdates = new HashMap<>();

	public TiledMap(World world) {
		this.world = world;

		outsideTile = new EmptyTile(world.getContentPack().getConstants().getOutsideTile());

		chunkDistance = world.isServer() ? GameConstants.KEEP_ALIVE_CHUNK_VIEW_DISTANCE : GameConstants.PLAYER_CHUNK_VIEW_DISTANCE;
		List<Tile> tilesById = world.getContentPack().getTiles();
		mapTilesById = new MapTile[tilesById.size()];
		for (int i = 0; i < tilesById.size(); i++) {
			mapTilesById[i] = new EmptyTile(tilesById.get(i));
		}
		addListener(limits);
	}

	public void addListener(TiledMapListener listener) {
		listeners.add(listener);
	}

	public void notifyListeners(Consumer<TiledMapListener> action) {
		listeners.forEach(action);
	}

	public MapTile tileAt(Vector2 position) {
		return tileAt((int) Math.floor(position.getX()), (int) Math.floor(position.getY()));
	}

	public MapTile tileAt(int x, int y) {
		Chunk chunk = chunkAt(x, y);
		if (chunk == null) {
			return outsideTile;
		} else {
			return chunk.tileAt(x, y);
		}
	}

	public void addChunk(Chunk chunk) {
		newChunks.add(chunk);
	}

	public void removeChunk(Chunk chunk) {
		toRemoveChunks.add(chunk);
	}

	public void addChunk(CompressedChunk compressed) {
		Chunk chunk = compressed.buildChunk();
		insertChunk(chunk);
	}

	public void addAllChunks(CompressedChunk[] compresseds) {
		for (CompressedChunk compressed : compresseds) {
			addChunk(compressed);
		}
	}

	private void insertChunk(Chunk chunk) {
		Chunk existingChunk = chunks.get(chunk.getPosition());
		if (existingChunk == null || chunk.getUpdateTimestamp() > existingChunk.getUpdateTimestamp()) {
			chunks.put(chunk.getPosition(), chunk);
			List<StructureUpdate> updates = pollStructureUpdates(chunk.getPosition());
			if (updates != null) {
				updates.forEach(u -> u.perform(chunk));
			}
			listeners.forEach(l -> l.chunkLoaded(chunk));
		}
	}

	private void unloadChunk(Chunk chunk) {
		chunks.remove(chunk.getPosition());
		listeners.forEach(l -> l.chunkUnloaded(chunk));
	}

	public Chunk chunkAt(double x, double y) {
		ChunkPosition position = new ChunkPosition((int) Math.floor(x / GameConstants.CHUNK_SIZE), (int) Math.floor(y / GameConstants.CHUNK_SIZE));
		return chunks.get(position);
	}

	public Chunk chunkAt(ChunkPosition position) {
		return chunks.get(position);
	}

	private ChunkPosition chunkPosition(Vector2 pos) {
		return new ChunkPosition((int) Math.floor(pos.getX() / GameConstants.CHUNK_SIZE), (int) Math.floor(pos.getY() / GameConstants.CHUNK_SIZE));
	}

	public void update() {
		synchronized (this) {
			toRemoveChunks.forEach(this::unloadChunk);
			toRemoveChunks.clear();
			newChunks.forEach(this::insertChunk);
			newChunks.clear();
		}
		world.getEntityPool().get(EntityGroup.PLAYER).forEach(p -> {
			PlayerEntity player = (PlayerEntity) p;
			ChunkPosition chunkPosition = chunkPosition(p.getPosition());
			if (!chunkPosition.equals(player.getChunkPosition())) {
				player.setChunkPosition(chunkPosition);
				listeners.forEach(l -> l.playerChangedChunk(player));

			}
			for (int x = chunkPosition.getX() - chunkDistance; x <= chunkPosition.getX() + chunkDistance; x++) {
				for (int y = chunkPosition.getY() - chunkDistance; y <= chunkPosition.getY() + chunkDistance; y++) {
					ChunkPosition position = new ChunkPosition(x, y);
					if (!chunks.containsKey(position)) {
						// Putting the position key is pretty important, it
						// prevent the chunk to be
						// requested every frame until it is generated.
						chunks.put(position, null);
						ChunkManager.getInstance().requestChunk(this, position);
					} else {
						Chunk chunk = chunks.get(position);
						if (chunk != null) {
							chunk.check();
						}
					}
				}
			}
		});

	}

	public void applyUpdate(StructureUpdate[] structureUpdates) {
		for (StructureUpdate structureUpdate : structureUpdates) {
			Chunk chunk = chunkAt(structureUpdate.getX(), structureUpdate.getY());
			if (chunk == null) {
				ChunkPosition position = new ChunkPosition(structureUpdate.getX(), structureUpdate.getY());
				List<StructureUpdate> waitingList = waitingStructureUpdates.get(position);
				if (waitingList == null) {
					waitingList = new ArrayList<>();
					waitingStructureUpdates.put(position, waitingList);
				}
				waitingList.add(structureUpdate);
			} else {
				structureUpdate.perform(chunk);
			}
		}
	}

	private List<StructureUpdate> pollStructureUpdates(ChunkPosition chunkPosition) {
		return waitingStructureUpdates.remove(chunkPosition);
	}

	public int chunkCount() {
		return chunks.size();
	}

	public boolean collide(Entity e) {
		return collide(e.getPosition().getX(), e.getPosition().getY(), e.getBoundingRadius());
	}

	public boolean collide(Entity e, double dx, double dy) {
		return collide(e.getPosition().getX() + dx, e.getPosition().getY() + dy, e.getBoundingRadius());
	}

	public boolean collide(double x, double y, double radius) {
		int tileX = (int) Math.floor(x - radius);
		int startY = (int) Math.floor(y - radius);
		double right = x + radius;
		int endX = (int) Math.floor(right);
		if (right == endX) {
			endX--;
		}
		double top = y + radius;
		int endY = (int) Math.floor(top);
		if (endY == top) {
			endY--;
		}
		for (; tileX <= endX; tileX++) {
			for (int tileY = startY; tileY <= endY; tileY++) {
				if (tileAt(tileX, tileY).isSolid()) {
					return true;
				}
			}
		}
		return false;
	}

}
