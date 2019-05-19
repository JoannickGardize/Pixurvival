package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.Collection;
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
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;

public class TiledMap {

	private List<TiledMapListener> listeners = new ArrayList<>();
	private List<PlayerMapEventListener> playerMapEventListeners = new ArrayList<>();
	private List<Chunk> newChunks = new ArrayList<>();
	private List<Chunk> toRemoveChunks = new ArrayList<>();

	@Getter
	private World world;

	private MapTile outsideTile;
	private double chunkDistance;
	@Getter
	private MapTile[] mapTilesById;

	@Getter
	private TiledMapLimits limits = new TiledMapLimits();

	private Map<ChunkPosition, Chunk> chunks = new HashMap<>();

	private Map<ChunkPosition, List<StructureUpdate>> waitingStructureUpdates = new HashMap<>();

	private @Getter ChunkRepository repository = new ChunkRepository();

	public TiledMap(World world) {
		this.world = world;

		outsideTile = new EmptyTile(world.getContentPack().getConstants().getOutsideTile());

		chunkDistance = world.isServer() ? GameConstants.KEEP_ALIVE_DISTANCE : GameConstants.PLAYER_VIEW_DISTANCE;
		List<Tile> tilesById = world.getContentPack().getTiles();
		mapTilesById = new MapTile[tilesById.size()];
		for (int i = 0; i < tilesById.size(); i++) {
			mapTilesById[i] = new EmptyTile(tilesById.get(i));
		}
		addListener(limits);
		if (world.isServer()) {
			addListener(new NaturalSpawnManager());
		}
	}

	public void addListener(TiledMapListener listener) {
		listeners.add(listener);
	}

	public void addPlayerMapEventListener(PlayerMapEventListener listener) {
		playerMapEventListeners.add(listener);
	}

	public void notifyEnterView(PlayerEntity player, ChunkPosition position) {
		playerMapEventListeners.forEach(l -> l.enterVision(player, position));
	}

	public void notifyExitView(PlayerEntity player, ChunkPosition position) {
		playerMapEventListeners.forEach(l -> l.exitVision(player, position));
	}

	public void notifyListeners(Consumer<TiledMapListener> action) {
		listeners.forEach(action);
	}

	public MapTile tileAt(Vector2 position) {
		return tileAt(MathUtils.floor(position.getX()), MathUtils.floor(position.getY()));
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

	private void addChunk(CompressedChunk compressed) {
		Chunk chunk = compressed.buildChunk();
		insertChunk(chunk);
	}

	public void addAllChunks(Collection<CompressedChunk> compresseds) {
		for (CompressedChunk compressed : compresseds) {
			addChunk(compressed);
		}
	}

	private void insertChunk(Chunk chunk) {
		Chunk existingChunk = chunks.get(chunk.getPosition());
		if (existingChunk == null || chunk.getUpdateTimestamp() > existingChunk.getUpdateTimestamp()) {
			chunks.put(chunk.getPosition(), chunk);
			// world.getEntityPool().sneakyAddAll(chunk.getEntities());
			List<StructureUpdate> updates = pollStructureUpdates(chunk.getPosition());
			if (updates != null) {
				updates.forEach(u -> u.perform(chunk));
			}
			listeners.forEach(l -> l.chunkLoaded(chunk));
		}
	}

	private void unloadChunk(Chunk chunk) {
		repository.save(chunk);
		chunks.remove(chunk.getPosition());
		world.getEntityPool().removeAll(chunk.getEntities());
		listeners.forEach(l -> l.chunkUnloaded(chunk));
	}

	public Chunk chunkAt(double x, double y) {
		ChunkPosition position = new ChunkPosition(MathUtils.floor(x / GameConstants.CHUNK_SIZE), MathUtils.floor(y / GameConstants.CHUNK_SIZE));
		return chunks.get(position);
	}

	public Chunk chunkAt(ChunkPosition position) {
		return chunks.get(position);
	}

	public void ifChunkExists(ChunkPosition position, Consumer<Chunk> action) {
		Chunk chunk = chunkAt(position);
		if (chunk != null) {
			action.accept(chunk);
		}
	}

	public void notifyChangedChunk(ChunkPosition previousPosition, Entity e) {
		listeners.forEach(l -> l.entityEnterChunk(previousPosition, e));
	}

	public void update() {
		synchronized (this) {
			toRemoveChunks.forEach(this::unloadChunk);
			toRemoveChunks.clear();
			newChunks.forEach(this::insertChunk);
			newChunks.clear();
		}
		if (world.isServer()) {
			world.getEntityPool().get(EntityGroup.PLAYER).forEach(this::checkPlayerChunks);
		} else {
			PlayerEntity myPlayer = world.getMyPlayer();
			if (myPlayer != null) {
				checkPlayerChunks(myPlayer);
			}
		}
	}

	private void checkPlayerChunks(Entity e) {
		if (e.getChunk() == null) {
			ChunkPosition chunkPosition = ChunkPosition.fromWorldPosition(e.getPosition());
			if (!chunks.containsKey(chunkPosition)) {
				requestChunk(chunkPosition);
			}
		} else {
			ChunkPosition.forEachChunkPosition(e.getPosition(), chunkDistance, position -> {
				if (!chunks.containsKey(position)) {
					// Putting the position key is very important, it
					// prevent the chunk to be
					// requested every frame until it is generated.
					requestChunk(position);
				} else {
					Chunk chunk = chunks.get(position);
					if (chunk != null) {
						chunk.check();
					}
				}
			});
		}
	}

	private void requestChunk(ChunkPosition position) {
		chunks.put(position, null);
		ChunkManager.getInstance().requestChunk(this, position);
	}

	public void forEachChunk(Vector2 center, double halfSquareLength, Consumer<Chunk> action) {
		ChunkPosition.forEachChunkPosition(center, halfSquareLength, position -> {
			Chunk chunk = chunks.get(position);
			if (chunk != null) {
				action.accept(chunk);
			}
		});
	}

	public void forEachEntities(EntityGroup group, Vector2 center, double radius, Consumer<Entity> action) {
		forEachChunk(center, radius, chunk -> {
			double radiusSquared = radius * radius;
			for (Entity e : chunk.getEntities().get(group)) {
				if (e.distanceSquared(center) <= radiusSquared) {
					action.accept(e);
				}
			}
		});
	}

	public void applyUpdate(Collection<StructureUpdate> structureUpdates) {
		for (StructureUpdate structureUpdate : structureUpdates) {
			Chunk chunk = chunkAt(structureUpdate.getX(), structureUpdate.getY());
			if (chunk == null) {
				ChunkPosition position = new ChunkPosition(structureUpdate.getX(), structureUpdate.getY());
				List<StructureUpdate> waitingList = waitingStructureUpdates.computeIfAbsent(position, p -> new ArrayList<>());
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
		return collide(e.getPosition().getX(), e.getPosition().getY(), e.getCollisionRadius());
	}

	public boolean collide(Entity e, double dx, double dy) {
		return collide(e.getPosition().getX() + dx, e.getPosition().getY() + dy, e.getCollisionRadius());
	}

	public boolean collide(double x, double y, double radius) {
		int tileX = MathUtils.floor(x - radius);
		int startY = MathUtils.floor(y - radius);
		double right = x + radius;
		int endX = MathUtils.floor(right);
		if (MathUtils.equals(right, endX)) {
			endX--;
		}
		double top = y + radius;
		int endY = MathUtils.floor(top);
		if (MathUtils.equals(endY, top)) {
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
