package com.pixurvival.core.map;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;
import com.pixurvival.core.map.chunk.update.StructureUpdate;

public interface TiledMapListener {

    void chunkLoaded(Chunk chunk);

    void chunkUnloaded(Chunk chunk);

    void structureChanged(StructureEntity mapStructure, StructureUpdate structureUpdate);

    void structureAdded(StructureEntity mapStructure);

    void structureRemoved(StructureEntity mapStructure);

    void entityEnterChunk(ChunkPosition previousPosition, Entity e);
}
