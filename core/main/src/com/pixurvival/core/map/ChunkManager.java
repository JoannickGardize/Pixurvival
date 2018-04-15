package com.pixurvival.core.map;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.EngineThread;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

public class ChunkManager extends EngineThread {

	private static final double UNLOAD_CHECK_RATE = 0.05;

	@RequiredArgsConstructor
	private static class TiledMapEntry {
		@NonNull
		TiledMap map;
		List<Position> requestedPositions = new ArrayList<>();
		Position checkPosition = new Position(0, 0);
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
			checkPosition = new Position(nextX, nextY);
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
	private List<Position> tmpPositions = new ArrayList<>();
	private List<Chunk> tmpChunks = new ArrayList<>();

	private ChunkManager() {
		super("Chunk Manager");
		setUpdatePerSecond(20);
		setMaxUpdatePerFrame(1);
		start();
	}

	public void requestChunk(TiledMap map, Position position) {
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

	@Override
	public void update(double deltaTimeMillis) {
		supplyChunks();
		unloadChunks();
	}

	@SneakyThrows
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
				if (file == null) {
					if (entry.map.getWorld().isServer()) {
						Chunk chunk = entry.map.getWorld().getChunkSupplier().get(p.getX(), p.getY());
						tmpChunks.add(chunk);
					} else {
						entry.map.addMissingChunk(p);
					}
				} else {
					try {
						byte[] data = Files.readAllBytes(file.toPath());
						tmpChunks.add(new CompressedChunk(entry.map, data).buildChunk());
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
		if (getLoad() < 0.3) {
			tiledMaps.values().forEach(entry -> {
				int unloadTry = (int) (entry.map.chunkCount() * UNLOAD_CHECK_RATE);
				for (int i = 0; i < unloadTry; i++) {
					entry.nextCheckPosition();
					synchronized (entry.map) {
						Chunk chunk = entry.map.chunkAt(entry.checkPosition);
						if (chunk != null && chunk.isTimeout()) {
							CompressedChunk compressedChunk = chunk.getCompressed();
							try {
								String fileName = entry.checkPosition.fileName();
								File file = new File(entry.map.getWorld().getSaveDirectory(), fileName);
								if (!file.exists()) {
									Files.write(file.toPath(), compressedChunk.getData());
									entry.allFiles.put(fileName, file);
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							entry.map.removeChunk(chunk);
						}
					}
				}
			});
		}
	}

}
