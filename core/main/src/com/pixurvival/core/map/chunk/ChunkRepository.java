package com.pixurvival.core.map.chunk;

import java.util.Collection;

public interface ChunkRepository {

	/**
	 * Optional operation.
	 * 
	 * @param compressedChunk
	 */
	void save(CompressedChunk compressedChunk);

	void save(Chunk chunk);

	ChunkRepositoryEntry load(ChunkPosition position);

	/**
	 * optional operation
	 * 
	 * @return
	 */
	Collection<ServerChunkRepositoryEntry> getAll();

	void add(ServerChunkRepositoryEntry data);
}
