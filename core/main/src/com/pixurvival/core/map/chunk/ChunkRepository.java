package com.pixurvival.core.map.chunk;

import java.util.HashMap;
import java.util.Map;

public class ChunkRepository {

	private Map<ChunkPosition, CompressedChunk> store = new HashMap<>();

	public void save(Chunk chunk) {
		store.put(chunk.getPosition(), chunk.getCompressed());
	}

	public Chunk load(ChunkPosition position) {
		CompressedChunk compressed = store.get(position);
		return compressed == null ? null : compressed.buildChunk();
	}
}
