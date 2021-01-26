package com.pixurvival.core.map.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.pixurvival.core.map.TiledMap;

import lombok.NonNull;
import lombok.Setter;

/**
 * Manage chunk loading and unloading of multiples {@link TiledMap}s. It runs in
 * a separated thread, to prevent FPS drop of the main game thread. It unload
 * chunks that are too far for a fixed amount of time, they are compressed and
 * kept in memory.
 * 
 * @author SharkHendrix
 *
 */
public class ChunkManager {

	private static final float UNLOAD_CHECK_RATE = 0.05f;

	@NonNull
	private final TiledMap map;
	private final BlockingQueue<ChunkPosition> requestedPositions = new LinkedBlockingDeque<>();
	private ChunkPosition checkPosition = new ChunkPosition(0, 0);
	private final List<ChunkManagerPlugin> plugins = Collections.synchronizedList(new ArrayList<>());
	private @Setter boolean running = true;

	public ChunkManager(TiledMap map) {
		this.map = map;
		new Thread(this::run, "Chunk Manager").start();
	}

	public void addPlugin(ChunkManagerPlugin plugin) {
		plugins.add(plugin);
	}

	public void removePlugin(ChunkManagerPlugin plugin) {
		plugins.remove(plugin);
	}

	public void requestChunk(ChunkPosition position) {
		try {
			requestedPositions.put(position);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}

	private void run() {
		while (running) {
			supplyChunks();
			if (requestedPositions.isEmpty()) {
				unloadChunks();
			}
		}
	}

	private void supplyChunks() {
		ChunkPosition chunkPosition;
		try {
			chunkPosition = requestedPositions.poll(500, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
			return;
		}
		if (chunkPosition == null) {
			return;
		}
		ChunkRepositoryEntry chunkEntry = map.getRepository().load(chunkPosition);
		if (chunkEntry == null && map.getWorld().isServer()) {
			Chunk chunk = map.getWorld().getChunkSupplier().get(chunkPosition.getX(), chunkPosition.getY());
			chunk.setNewlyCreated(true);
			chunkEntry = new ChunkRepositoryEntry(chunk);
		}
		if (chunkEntry != null) {
			for (ChunkManagerPlugin plugin : plugins) {
				plugin.chunkLoaded(chunkEntry.getChunk());
			}
			synchronized (map) {
				map.addChunk(chunkEntry);
				map.notifyChunkAvailable(chunkEntry.getChunk().getPosition());
			}
		}
	}

	private void unloadChunks() {
		int unloadTry = Math.max(5, (int) (map.chunkCount() * UNLOAD_CHECK_RATE));
		synchronized (map) {
			for (int i = 0; i < unloadTry; i++) {
				nextCheckPosition();
				Chunk chunk = map.chunkAt(checkPosition);
				if (chunk != null && chunk.isTimeout()) {
					map.removeChunk(chunk);
				}
			}
		}
	}

	private void nextCheckPosition() {
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
