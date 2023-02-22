package com.pixurvival.core.map.generator;

import com.pixurvival.core.map.chunk.Chunk;

public interface ChunkPostProcessor {

    void apply(Chunk chunk);
}
