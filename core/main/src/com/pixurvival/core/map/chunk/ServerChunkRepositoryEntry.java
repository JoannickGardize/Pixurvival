package com.pixurvival.core.map.chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerChunkRepositoryEntry {
    private long time;
    private CompressedChunk compressedChunk;
    private byte[] entityData;
}
