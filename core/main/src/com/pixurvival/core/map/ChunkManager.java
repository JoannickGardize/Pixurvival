package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.EngineThread;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Manage chunk generating and unloading of multiples {@link TiledMap}s. It run
 * in a separated thread, to prevent FPS drop of the main game thread. Unload
 * chunks that are too far for a fixed amount of time, they are compressed and
 * stored in local files.
 * 
 * @author jojog
 *
 */
public class ChunkManager extends EngineThread {

	private static final double UNLOAD_CHECK_RATE = 0.05;

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

	private static @Getter ChunkManager instance = new ChunkManager();

	private final Map<TiledMap, TiledMapEntry> tiledMaps = new HashMap<>();
	private final List<ChunkPosition> tmpPositions = new ArrayList<>();
	private final List<Chunk> tmpChunks = new ArrayList<>();

	private ChunkManager() {
		super("Chunk Manager");
		setUpdatePerSecond(20);
		setMaxUpdatePerFrame(1);
		start();
	}

	public void requestChunk(TiledMap map, ChunkPosition position) {
		synchronized (map) {
			TiledMapEntry entry = tiledMaps.computeIfAbsent(map, m -> new TiledMapEntry(m));
			entry.requestedPositions.add(position);
		}
	}

	public void stopManaging(final TiledMap map) {
		synchronized (tiledMaps) {
			tiledMaps.remove(map);
		}
	}

	@Override
	public void update(double deltaTimeMillis) {
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
			tmpChunks.clear();
			tmpPositions.forEach(p -> {
				Chunk chunk = entry.map.getRepository().load(p);
				if (chunk == null && entry.map.getWorld().isServer()) {
					chunk = entry.map.getWorld().getChunkSupplier().get(p.getX(), p.getY());
					chunk.setNewlyCreated(true);
				}
				if (chunk != null) {
					tmpChunks.add(chunk);
				}
			});

			synchronized (entry.map) {
				tmpChunks.forEach(c -> entry.map.addChunk(c));
			}
		});
	}

	private void unloadChunks() {
		if (getLoad() > 0.3) {
			return;
		}
		tiledMaps.values().forEach(entry -> {
			int unloadTry = (int) (entry.map.chunkCount() * UNLOAD_CHECK_RATE);
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
