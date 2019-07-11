package com.pixurvival.core.map;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;

public interface TiledMapListener {

	void chunkLoaded(Chunk chunk);

	void chunkUnloaded(Chunk chunk);

	void structureChanged(MapStructure mapStructure);

	void structureAdded(MapStructure mapStructure);

	void structureRemoved(MapStructure mapStructure);

	void entityEnterChunk(ChunkPosition previousPosition, Entity e);
}
