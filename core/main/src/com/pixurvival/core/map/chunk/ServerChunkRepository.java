package com.pixurvival.core.map.chunk;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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

		public static class Serializer extends com.esotericsoftware.kryo.Serializer<ChunkEntry> {

			@Override
			public void write(Kryo kryo, Output output, ChunkEntry object) {
				kryo.writeObject(output, object.compressedChunk);
				kryo.writeObject(output, object.entitiesData);
			}

			@Override
			public ChunkEntry read(Kryo kryo, Input input, Class<ChunkEntry> type) {
				return new ChunkEntry(kryo.readObject(input, CompressedChunk.class),
						kryo.readObject(input, byte[].class));
			}

		}
	}

	private ByteBuffer entityByteBuffer = ByteBuffer.allocate(WorldUpdate.BUFFER_SIZE * 2);

	private Map<ChunkPosition, ChunkEntry> store = new HashMap<>();

	@Override
	public void save(Chunk chunk) {
		chunk.getMap().getWorld().getEntityPool().removeAll(chunk.getEntities());
		store.put(chunk.getPosition(), writeChunkEntry(chunk));
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

	public Collection<ChunkEntry> getEntries() {
		return store.values();
	}

	public void put(ChunkEntry entry) {
		store.put(entry.getCompressedChunk().getPosition(), entry);
	}

	public ChunkEntry writeChunkEntry(Chunk chunk) {
		entityByteBuffer.position(0);
		entityByteBuffer.put(EntityGroup.END_MARKER);
		chunk.getEntities().writeRepositoryUpdate(entityByteBuffer);
		entityByteBuffer.put(EntityGroup.END_MARKER);
		entityByteBuffer.putShort((short) 0);
		byte[] bufferArray = entityByteBuffer.array();
		byte[] chunkEntities = Arrays.copyOf(bufferArray, bufferArray.length);
		return new ChunkEntry(chunk.getCompressed(), chunkEntities);
	}

}
