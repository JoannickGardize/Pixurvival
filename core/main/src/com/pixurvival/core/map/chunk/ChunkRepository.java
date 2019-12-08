package com.pixurvival.core.map.chunk;

public interface ChunkRepository {

	void save(Chunk chunk);

	ChunkRepositoryEntry load(ChunkPosition position);
}
