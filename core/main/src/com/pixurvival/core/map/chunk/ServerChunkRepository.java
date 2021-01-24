package com.pixurvival.core.map.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.message.WorldUpdate;

/**
 * Chunk repository for server and local games, stores the chunk map and its
 * entities. Players are never stored, since they have their own special
 * management.
 * 
 * @author SharkHendrix
 *
 */
public class ServerChunkRepository implements ChunkRepository {

	private ByteBuffer entityByteBuffer = ByteBuffer.allocate(WorldUpdate.BUFFER_SIZE * 2);

	private Map<ChunkPosition, ServerChunkRepositoryEntry> store = new HashMap<>();

	@Override
	public synchronized void save(Chunk chunk) {
		entityByteBuffer.position(0);
		entityByteBuffer.put(EntityGroup.END_MARKER);
		chunk.getMap().getWorld().getTime().setSerializationContextTimeToNow();
		Map<Long, Entity> players = chunk.getEntities().removeGroup(EntityGroup.PLAYER);
		chunk.getEntities().writeRepositoryUpdate(entityByteBuffer);
		if (players != null) {
			chunk.getEntities().setGroup(EntityGroup.PLAYER, players);
		}
		entityByteBuffer.put(EntityGroup.END_MARKER);
		entityByteBuffer.put((byte) 0);
		byte[] bufferArray = entityByteBuffer.array();
		byte[] chunkEntities = Arrays.copyOf(bufferArray, entityByteBuffer.position());
		store.put(chunk.getPosition(), new ServerChunkRepositoryEntry(chunk.getMap().getWorld().getTime().getTimeMillis(), chunk.getCompressed(), chunkEntities));
	}

	@Override
	public synchronized ChunkRepositoryEntry load(ChunkPosition position) {
		ServerChunkRepositoryEntry entry = store.get(position);
		if (entry == null) {
			return null;
		} else {
			Chunk chunk = entry.getCompressedChunk().buildChunk();
			ByteBuffer byteBuffer = ByteBuffer.wrap(entry.getEntityData());
			return new ChunkRepositoryEntry(chunk, byteBuffer, entry.getTime());
		}
	}

	@Override
	public Collection<ServerChunkRepositoryEntry> getAll() {
		return Collections.unmodifiableCollection(store.values());
	}

	@Override
	public synchronized void add(ServerChunkRepositoryEntry data) {
		store.put(data.getCompressedChunk().getPosition(), data);
	}

	@Override
	public void save(CompressedChunk compressedChunk) {
		throw new UnsupportedOperationException();
	}
}
