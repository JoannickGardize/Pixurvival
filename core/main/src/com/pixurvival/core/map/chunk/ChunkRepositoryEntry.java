package com.pixurvival.core.map.chunk;

import com.pixurvival.core.entity.EntityGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public class ChunkRepositoryEntry {

    private Chunk chunk;
    private ByteBuffer entityData;
    private long time;

    public ChunkRepositoryEntry(Chunk chunk) {
        this.chunk = chunk;
        entityData = emptyByteArray();
    }

    private ByteBuffer emptyByteArray() {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.put(EntityGroup.END_MARKER);
        bb.put(EntityGroup.END_MARKER);
        bb.put((byte) 0);
        bb.position(0);
        return bb;
    }
}
