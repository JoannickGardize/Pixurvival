package com.pixurvival.core.map.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.message.WorldUpdate;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ServerChunkRepository implements ChunkRepository {

	@Getter
	@AllArgsConstructor
	public static class ChunkEntry {
		private CompressedChunk compressedChunk;
		private byte[] entitiesData;
	}

	private ByteBuffer entityByteBuffer = ByteBuffer.allocate(WorldUpdate.BUFFER_SIZE * 2);

	private Map<ChunkPosition, ChunkEntry> store = new HashMap<>();

	@Override
	public void save(Chunk chunk) {
		// TODO mettre ailleurs la suppression des entités ?
		chunk.getMap().getWorld().getEntityPool().removeAll(chunk.getEntities());
		entityByteBuffer.position(0);
		entityByteBuffer.put(EntityGroup.END_MARKER);
		chunk.getEntities().writeRepositoryUpdate(entityByteBuffer);
		entityByteBuffer.put(EntityGroup.END_MARKER);
		entityByteBuffer.putShort((short) 0);
		byte[] bufferArray = entityByteBuffer.array();
		byte[] chunkEntities = Arrays.copyOf(bufferArray, bufferArray.length);
		store.put(chunk.getPosition(), new ChunkEntry(chunk.getCompressed(), chunkEntities));
	}

	@Override
	public ChunkRepositoryEntry load(ChunkPosition position) {
		ChunkEntry entry = store.get(position);
		if (entry == null) {
			return null;
		} else {
			Chunk chunk = entry.getCompressedChunk().buildChunk();
			ByteBuffer byteBuffer = ByteBuffer.wrap(entry.entitiesData);
			return new ChunkRepositoryEntry(chunk, byteBuffer);
		}
	}
}
