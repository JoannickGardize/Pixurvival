package com.pixurvival.core.map.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.EngineThread;
import com.pixurvival.core.map.TiledMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Manage chunk loading and unloading of multiples {@link TiledMap}s. It run in
 * a separated thread, to prevent FPS drop of the main game thread. It unload
 * chunks that are too far for a fixed amount of time, they are compressed and
 * kept in memory.
 * 
 * @author SharkHendrix
 *
 */
// TODO No more thread engine, one thread per world
public class ChunkManager extends EngineThread {

	private static final float UNLOAD_CHECK_RATE = 0.05f;

	@RequiredArgsConstructor
	private static class TiledMapEntry {
		@NonNull
		TiledMap map;
		List<ChunkPosition> requestedPositions = new ArrayList<>();
		ChunkPosition checkPosition = new ChunkPosition(0, 0);

		void nextCheckPosition() {
			int nextX = checkPosition.getX() + 1;
			int nextY = checkPosition.getY();
			if (nextX > map.getLimits().getXMax()) {
				nextX = map.getLimits().getXMin();
				nextY++;
				if (nextY > map.getLimits().getYMax()) {
					nextY = map.getLimits().getYMin();
				}
			}
			checkPosition = new ChunkPosition(nextX, nextY);
		}
	}

	// TODO no more singleton
	private static final @Getter ChunkManager instance = new ChunkManager();

	private final Map<TiledMap, TiledMapEntry> tiledMaps = new HashMap<>();
	private final List<ChunkPosition> tmpPositions = new ArrayList<>();
	private final List<ChunkRepositoryEntry> tmpRepositoryEntries = new ArrayList<>();
	private final List<ChunkManagerPlugin> plugins = Collections.synchronizedList(new ArrayList<>());

	private ChunkManager() {
		super("Chunk Manager");
		setUpdatePerSecond(20);
		setMaxUpdatePerFrame(1);
		start();
	}

	public void addPlugin(ChunkManagerPlugin plugin) {
		plugins.add(plugin);
	}

	public void removePlugin(ChunkManagerPlugin plugin) {
		plugins.remove(plugin);
	}

	public void requestChunk(TiledMap map, ChunkPosition position) {
		synchronized (tiledMaps) {
			TiledMapEntry entry = tiledMaps.computeIfAbsent(map, TiledMapEntry::new);
			entry.requestedPositions.add(position);
		}
	}

	public void stopManaging(final TiledMap map) {
		synchronized (tiledMaps) {
			tiledMaps.remove(map);
		}
	}

	@Override
	public void update(float deltaTimeMillis) {
		synchronized (tiledMaps) {
			supplyChunks();
			unloadChunks();
		}
	}

	private void supplyChunks() {
		tiledMaps.values().forEach(entry -> {
			tmpPositions.clear();
			synchronized (entry.map) {
				tmpPositions.addAll(entry.requestedPositions);
				entry.requestedPositions.clear();
			}
			tmpRepositoryEntries.clear();
			tmpPositions.forEach(p -> {
				ChunkRepositoryEntry chunkEntry = entry.map.getRepository().load(p);
				if (chunkEntry == null && entry.map.getWorld().isServer()) {
					Chunk chunk = entry.map.getWorld().getChunkSupplier().get(p.getX(), p.getY());
					chunk.setNewlyCreated(true);
					chunkEntry = new ChunkRepositoryEntry(chunk);
				}
				if (chunkEntry != null) {
					tmpRepositoryEntries.add(chunkEntry);
				}
			});
			tmpRepositoryEntries.forEach(e -> plugins.forEach(p -> p.chunkLoaded(e.getChunk())));
			synchronized (entry.map) {
				tmpRepositoryEntries.forEach(c -> {
					entry.map.addChunk(c);
					entry.map.notifyChunkAvailable(c.getChunk().getPosition());
				});
			}
		});
	}

	private void unloadChunks() {
		if (getLoad() > 0.3) {
			return;
		}
		tiledMaps.values().forEach(entry -> {
			int unloadTry = Math.max(5, (int) (entry.map.chunkCount() * UNLOAD_CHECK_RATE));

			synchronized (entry.map) {
				for (int i = 0; i < unloadTry; i++) {
					entry.nextCheckPosition();
					Chunk chunk = entry.map.chunkAt(entry.checkPosition);
					if (chunk != null && chunk.isTimeout()) {
						entry.map.removeChunk(chunk);
					}
				}
			}
		});
	}
}
