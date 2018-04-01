package com.pixurvival.core.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.EngineThread;

import lombok.Getter;

public class ChunkManager extends EngineThread {

	private @Getter static ChunkManager instance = new ChunkManager();

	private Map<TiledMap, List<Position>> tiledMaps = new HashMap<>();
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
			List<Position> positions = tiledMaps.get(map);
			if (positions == null) {
				positions = new ArrayList<>();
				tiledMaps.put(map, positions);
			}
			positions.add(position);
		}
	}

	@Override
	public void update(double deltaTimeMillis) {
		tiledMaps.forEach((map, positions) -> {
			tmpPositions.clear();
			synchronized (map) {
				tmpPositions.addAll(positions);
				positions.clear();
			}
			tmpChunks.clear();
			tmpPositions.forEach(p -> {
				Chunk chunk = map.getWorld().getChunkSupplier().get(p.getX(), p.getY());
				tmpChunks.add(chunk);
			});
			synchronized (map) {
				tmpChunks.forEach(c -> map.addChunk(c));
			}
			positions.clear();
		});
	}

}
