package com.pixurvival.core.map.chunk;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Chunk repository for multiplayer client game, only stores the chunk map.
 *
 * @author SharkHendrix
 */
public class ClientChunkRepository implements ChunkRepository {

    private Map<ChunkPosition, CompressedChunk> store = new HashMap<>();

    @Override
    public synchronized void save(CompressedChunk compressedChunk) {
        store.put(compressedChunk.getPosition(), compressedChunk);
    }

    @Override
    public synchronized void save(Chunk chunk) {
        store.put(chunk.getPosition(), chunk.getCompressed());
    }

    @Override
    public synchronized ChunkRepositoryEntry load(ChunkPosition position) {
        CompressedChunk compressed = store.get(position);
        return compressed == null ? null : new ChunkRepositoryEntry(compressed.buildChunk());
    }

    @Override
    public Collection<ServerChunkRepositoryEntry> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(ServerChunkRepositoryEntry data) {
        throw new UnsupportedOperationException();
    }
}
