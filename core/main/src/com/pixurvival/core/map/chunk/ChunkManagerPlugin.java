package com.pixurvival.core.map.chunk;

/**
 * Listener class of the {@link ChunkManager}, which allows additional chunk
 * computation in the {@link ChunkManager} thread itself.
 *
 * @author SharkHendrix
 */
public interface ChunkManagerPlugin {

    void chunkLoaded(Chunk chunk);
}
