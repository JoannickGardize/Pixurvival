package com.pixurvival.core.map.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.message.WorldUpdate;

/**
 * Chunk repository for server and local games.
 * 
 * @author SharkHendrix
 *
 */
public class ServerChunkRepository implements ChunkRepository {

	private ByteBuffer entityByteBuffer = ByteBuffer.allocate(WorldUpdate.BUFFER_SIZE * 2);

	private Map<ChunkPosition, CompressedChunkAndEntityData> store = new HashMap<>();

	@Override
	public synchronized void save(Chunk chunk) {
		entityByteBuffer.position(0);
		entityByteBuffer.put(EntityGroup.END_MARKER);
		chunk.getEntities().writeRepositoryUpdate(entityByteBuffer);
		entityByteBuffer.put(EntityGroup.END_MARKER);
		entityByteBuffer.putShort((short) 0);
		byte[] bufferArray = entityByteBuffer.array();
		byte[] chunkEntities = Arrays.copyOf(bufferArray, entityByteBuffer.position());
		store.put(chunk.getPosition(), new CompressedChunkAndEntityData(chunk.getCompressed(), chunkEntities));
	}

	@Override
	public synchronized ChunkRepositoryEntry load(ChunkPosition position) {
		CompressedChunkAndEntityData entry = store.get(position);
		if (entry == null) {
			return null;
		} else {
			Chunk chunk = entry.getCompressedChunk().buildChunk();
			ByteBuffer byteBuffer = ByteBuffer.wrap(entry.getEntityData());
			return new ChunkRepositoryEntry(chunk, byteBuffer);
		}
	}

	@Override
	public Collection<CompressedChunkAndEntityData> getAll() {
		return Collections.unmodifiableCollection(store.values());
	}

	@Override
	public void add(CompressedChunkAndEntityData data) {
		store.put(data.getCompressedChunk().getPosition(), data);
	}
}
