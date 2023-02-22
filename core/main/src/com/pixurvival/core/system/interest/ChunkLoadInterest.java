package com.pixurvival.core.system.interest;

import com.pixurvival.core.map.chunk.Chunk;

public interface ChunkLoadInterest extends Interest {

    void chunkLoaded(Chunk chunk);

    void chunkUnloaded(Chunk chunk);
}
