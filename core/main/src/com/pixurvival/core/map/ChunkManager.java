package com.pixurvival.core.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.EngineThread;
import com.pixurvival.core.util.FileUtils;

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
		Map<String, File> allFiles = new HashMap<>();

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

		void refreshList() {
			allFiles.clear();
			File saveDir = map.getWorld().getSaveDirectory();
			for (File file : saveDir.listFiles()) {
				allFiles.put(file.getName(), file);
			}
		}
	}

	private @Getter static ChunkManager instance = new ChunkManager();

	private Map<TiledMap, TiledMapEntry> tiledMaps = new HashMap<>();
	private List<ChunkPosition> tmpPositions = new ArrayList<>();
	private List<Chunk> tmpChunks = new ArrayList<>();

	private ChunkManager() {
		super("Chunk Manager");
		setUpdatePerSecond(20);
		setMaxUpdatePerFrame(1);
		start();
	}

	public void requestChunk(final TiledMap map, ChunkPosition position) {
		synchronized (map) {
			TiledMapEntry entry = tiledMaps.get(map);
			if (entry == null) {
				entry = new TiledMapEntry(map);
				entry.refreshList();
				tiledMaps.put(map, entry);
			}
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
				File file = entry.allFiles.get(p.fileName());
				Chunk chunk = null;
				if (file == null) {
					if (entry.map.getWorld().isServer()) {
						chunk = entry.map.getWorld().getChunkSupplier().get(p.getX(), p.getY());
						tmpChunks.add(chunk);
					}
				} else {
					try {
						byte[] data = FileUtils.readBytes(file);
						chunk = new CompressedChunk(entry.map, data).buildChunk();
						chunk.setFileSync(true);
						tmpChunks.add(chunk);
					} catch (IOException e) {
						e.printStackTrace();
					}
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
			tmpChunks.clear();
			synchronized (entry.map) {
				for (int i = 0; i < unloadTry; i++) {
					entry.nextCheckPosition();
					Chunk chunk = entry.map.chunkAt(entry.checkPosition);
					if (chunk != null && chunk.isTimeout()) {
						tmpChunks.add(chunk);
						entry.map.removeChunk(chunk);
					}
				}
			}
			tmpChunks.forEach(chunk -> {
				CompressedChunk compressedChunk = chunk.getCompressed();
				try {
					String fileName = chunk.getPosition().fileName();
					File file = new File(entry.map.getWorld().getSaveDirectory(), fileName);
					if (!file.exists() || !chunk.isFileSync()) {
						FileUtils.writeBytes(file, compressedChunk.getData());
						entry.allFiles.put(fileName, file);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		});
	}

}
