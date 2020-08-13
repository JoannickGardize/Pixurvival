package com.pixurvival.core.map.chunk;

import java.util.Collection;

public interface ChunkRepository {

	void save(Chunk chunk);

	ChunkRepositoryEntry load(ChunkPosition position);

	Collection<CompressedChunkAndEntityData> getAll();

	void add(CompressedChunkAndEntityData data);
}
