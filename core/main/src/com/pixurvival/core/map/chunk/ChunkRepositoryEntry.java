package com.pixurvival.core.map.chunk;

import java.nio.ByteBuffer;

import com.pixurvival.core.entity.EntityGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChunkRepositoryEntry {

	private Chunk chunk;
	private ByteBuffer entitiesData;

	public ChunkRepositoryEntry(Chunk chunk) {
		this.chunk = chunk;
		entitiesData = emptyByteArray();
	}

	private ByteBuffer emptyByteArray() {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.put(EntityGroup.END_MARKER);
		bb.put(EntityGroup.END_MARKER);
		bb.putShort((short) 0);
		bb.position(0);
		return bb;
	}
}
