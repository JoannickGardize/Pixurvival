package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkManager;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.ChunkRepository;
import com.pixurvival.core.map.chunk.ChunkRepositoryEntry;
import com.pixurvival.core.map.chunk.ClientChunkRepository;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.ServerChunkRepository;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;

import lombok.Getter;

public class TiledMap {

	private List<TiledMapListener> listeners = new ArrayList<>();
	private List<PlayerMapEventListener> playerMapEventListeners = new ArrayList<>();
	private List<ChunkRepositoryEntry> newChunks = new ArrayList<>();
	private List<Chunk> toRemoveChunks = new ArrayList<>();

	@Getter
	private World world;

	private MapTile outsideTile;
	private float chunkDistance;
	@Getter
	private MapTile[] mapTilesById;

	@Getter
	private TiledMapLimits limits = new TiledMapLimits();

	private Map<ChunkPosition, Chunk> chunks = new HashMap<>();

	private Map<ChunkPosition, List<StructureUpdate>> waitingStructureUpdates = new HashMap<>();

	private Map<ChunkPosition, ChunkPosition> waitingPositions = new ConcurrentHashMap<>();

	private @Getter ChunkRepository repository;

	private Light tmpResult;

	public TiledMap(World world) {
		this.world = world;

		if (world.isServer()) {
			repository = new ServerChunkRepository();
			outsideTile = new EmptyTile(world.getContentPack().getConstants().getOutsideTile());
		} else {
			repository = new ClientChunkRepository();
			outsideTile = new EmptyTile(world.getContentPack().getConstants().getOutsideTile()) {
				@Override
				public boolean isSolid() {
					// Force outside tile to not be solid, so distant allies
					// players
					// will move correctly.
					return false;
				}
			};

		}

		chunkDistance = world.isServer() ? GameConstants.KEEP_ALIVE_DISTANCE : GameConstants.PLAYER_VIEW_DISTANCE;
		List<Tile> tilesById = world.getContentPack().getTiles();
		mapTilesById = new MapTile[tilesById.size()];
		for (int i = 0; i < tilesById.size(); i++) {
			mapTilesById[i] = new EmptyTile(tilesById.get(i));
		}
		addListener(limits);
		if (world.isServer()) {
			addListener(world.getChunkCreatureSpawnManager());
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

	/**
	 * @param x
	 * @param y
	 * @return The tile at the given position. If the chunk of the tile is not
	 *         generated, returns the default tile of the {@link MapGenerator}.
	 */
	public MapTile tileAt(int x, int y) {
		Chunk chunk = chunkAt(x, y);
		if (chunk == null) {
			return outsideTile;
		} else {
			return chunk.tileAt(x, y);
		}
	}

	public void addChunk(ChunkRepositoryEntry chunk) {
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
		if (existingChunk == null || chunk.getUpdateTimestamp() >= existingChunk.getUpdateTimestamp()) {
			chunks.put(chunk.getPosition(), chunk);
			List<StructureUpdate> updates = pollStructureUpdates(chunk.getPosition());
			if (updates != null) {
				updates.forEach(u -> u.apply(chunk));
			}
			chunk.check();
			listeners.forEach(l -> l.chunkLoaded(chunk));
		} else {
			existingChunk.check();
		}
	}

	private void unloadChunk(Chunk chunk) {
		getWorld().getEntityPool().removeAll(chunk.getEntities());
		repository.save(chunk);
		chunks.remove(chunk.getPosition());
		listeners.forEach(l -> l.chunkUnloaded(chunk));
	}

	public Chunk chunkAt(float x, float y) {
		ChunkPosition position = new ChunkPosition(MathUtils.floor(x / GameConstants.CHUNK_SIZE), MathUtils.floor(y / GameConstants.CHUNK_SIZE));
		return chunks.get(position);
	}

	/**
	 * @param position
	 * @return The chunk at the given position, or null if still not generated.
	 */
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
		flushChunks();
		if (world.isServer()) {
			world.getEntityPool().get(EntityGroup.PLAYER).forEach(this::checkPlayerChunks);
		} else {
			Entity myPlayer = world.getMyPlayer();
			if (myPlayer != null) {
				checkPlayerChunks(myPlayer);
			}
		}
	}

	private void flushChunks() {
		synchronized (this) {
			toRemoveChunks.forEach(this::unloadChunk);
			toRemoveChunks.clear();
			newChunks.forEach(chunkEntry -> {
				world.getEntityPool().applyUpdate(chunkEntry.getEntityData(), world, true);
				insertChunk(chunkEntry.getChunk());
			});
			newChunks.clear();
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

	public void requestChunk(ChunkPosition position) {
		if (!chunks.containsKey(position)) {
			chunks.put(position, null);
			ChunkManager.getInstance().requestChunk(this, position);
		}
	}

	public void notifyChunkAvailable(ChunkPosition position) {
		ChunkPosition positionLock = waitingPositions.remove(position);
		if (positionLock != null) {
			synchronized (positionLock) {
				positionLock.notifyAll();
			}
		}
	}

	/**
	 * Returns the chunk at the given position, waiting for it if necessary.
	 * 
	 * @param position
	 *            The position of the requested chunk
	 * @return The chunk at the given position
	 */
	public Chunk chunkAtWait(ChunkPosition position) {
		requestChunk(position);
		flushChunks();
		ChunkPosition positionLock = waitingPositions.computeIfAbsent(new ChunkPosition(position), p -> p);
		synchronized (positionLock) {
			Chunk chunk = chunkAt(position);
			if (chunk != null) {
				return chunk;
			}
			waitingPositions.put(positionLock, positionLock);
			while ((chunk = chunkAt(position)) == null) {
				try {
					// TODO ???
					Log.info("Waiting for chunk at " + position);
					positionLock.wait(100);
				} catch (InterruptedException e) {
					Log.error("Error when waiting chunk", e);
					Thread.currentThread().interrupt();
					return null;
				}
				flushChunks();
			}
			System.out.println("end wait");
			return chunk;
		}
	}

	public void forEachChunk(Vector2 center, float halfSquareLength, Consumer<Chunk> action) {
		ChunkPosition.forEachChunkPosition(center, halfSquareLength, position -> {
			Chunk chunk = chunks.get(position);
			if (chunk != null) {
				action.accept(chunk);
			}
		});
	}

	public boolean forEachChunk(Vector2 center, float halfSquareLength, Predicate<Chunk> action) {
		return ChunkPosition.forEachChunkPosition(center, halfSquareLength, position -> {
			Chunk chunk = chunks.get(position);
			return chunk != null && action.test(chunk);
		});
	}

	public void forEachEntities(EntityGroup group, Vector2 center, float radius, Consumer<Entity> action) {
		forEachChunk(center, radius, chunk -> {
			float radiusSquared = radius * radius;
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
				structureUpdate.apply(chunk);
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

	public boolean collide(Entity e, float dx, float dy) {
		return collide(e.getPosition().getX() + dx, e.getPosition().getY() + dy, e.getCollisionRadius());
	}

	public boolean collide(float x, float y, float radius) {
		int tileX = MathUtils.floor(x - radius);
		int startY = MathUtils.floor(y - radius);
		float right = x + radius;
		int endX = MathUtils.floor(right);
		if (MathUtils.equals(right, endX)) {
			endX--;
		}
		float top = y + radius;
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

	public void forEachTile(float x, float y, float radius, Consumer<MapTile> action) {
		int tileX = MathUtils.floor(x - radius);
		int startY = MathUtils.floor(y - radius);
		float right = x + radius;
		int endX = MathUtils.floor(right);
		if (MathUtils.equals(right, endX)) {
			endX--;
		}
		float top = y + radius;
		int endY = MathUtils.floor(top);
		if (MathUtils.equals(endY, top)) {
			endY--;
		}
		for (; tileX <= endX; tileX++) {
			for (int tileY = startY; tileY <= endY; tileY++) {
				action.accept(tileAt(tileX, tileY));
			}
		}
	}

	private static class FindClosestStructureRun {
		MapStructure structure;
		float distance = Float.POSITIVE_INFINITY;
	}

	public MapStructure findClosestStructure(Vector2 position, float searchRadius, IntPredicate groupFilter, Predicate<MapStructure> structureFilter) {
		FindClosestStructureRun run = new FindClosestStructureRun();
		forEachChunk(position, searchRadius, chunk -> chunk.getStructures().forEach(entry -> {
			if (groupFilter.test(entry.getKey())) {
				for (MapStructure structure : entry.getValue()) {
					if (structureFilter.test(structure)) {
						float distance = position.distanceSquared(structure.getPosition());
						if (distance <= searchRadius * searchRadius && distance < run.distance) {
							run.structure = structure;
							run.distance = distance;
						}
					}
				}
			}
		}));
		return run.structure;
	}

	public MapStructure findClosestStructure(Vector2 position, float searchRadius) {
		return findClosestStructure(position, searchRadius, type -> true, s -> true);
	}

	public MapStructure findClosestStructure(Vector2 position, float searchRadius, Collection<Integer> structureTypeIds) {
		return findClosestStructure(position, searchRadius, structureTypeIds::contains, s -> true);
	}

	public MapStructure findClosestStructure(Vector2 position, float searchRadius, Collection<Integer> structureTypeIds, Predicate<MapStructure> filter) {
		return findClosestStructure(position, searchRadius, structureTypeIds::contains, filter);
	}

	public MapStructure findClosestStructure(Vector2 position, float searchRadius, int structureTypeId) {
		return findClosestStructure(position, searchRadius, t -> t == structureTypeId, s -> true);
	}

	public boolean isInAnyLight(Vector2 position) {
		return getAnyCollidingLight(position) != null;
	}

	public Light getAnyCollidingLight(Vector2 position) {
		tmpResult = null;
		forEachChunk(position, getWorld().getContentPack().getMaxLightRadius(), chunk -> {
			for (Light light : chunk.getLights()) {
				if (position.distanceSquared(light.getPosition()) <= light.getRadius() * light.getRadius()) {
					tmpResult = light;
					return true;
				}
			}
			return false;
		});
		return tmpResult;
	}

	public void saveAll() {
		for (Entry<ChunkPosition, List<StructureUpdate>> updateEntry : waitingStructureUpdates.entrySet()) {
			Chunk chunk = chunkAtWait(updateEntry.getKey());
			updateEntry.getValue().forEach(update -> update.apply(chunk));
		}
		waitingStructureUpdates.clear();
		synchronized (repository) {
			chunks.values().forEach(c -> repository.save(c));
		}
	}
}
