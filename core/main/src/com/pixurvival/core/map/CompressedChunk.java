package com.pixurvival.core.map;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Structure;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CompressedChunk {

	private static ThreadLocal<ByteBuffer> bufferLocal = ThreadLocal.withInitial(() -> {
		ByteBuffer buffer = ByteBuffer.allocate(4096);
		buffer.mark();
		return buffer;
	});

	private byte[] data;

	public CompressedChunk(Chunk chunk) {

		ByteBuffer buffer = bufferLocal.get();
		buffer.reset();
		buffer.putInt(chunk.getPosition().getX());
		buffer.putInt(chunk.getPosition().getY());
		MapTile currentTile = null;
		byte currentLength = 0;
		for (MapTile tile : chunk.getTiles()) {
			if (tile != currentTile || currentLength == Byte.MAX_VALUE) {
				if (currentTile != null) {
					buffer.put(currentLength);
					buffer.put(currentTile.getTileDefinition().getId());
				}
				currentLength = 1;
				currentTile = tile;
			} else {
				currentLength++;
			}
		}
		buffer.put(currentLength);
		buffer.put(currentTile.getTileDefinition().getId());
		buffer.putShort((short) chunk.getStructures().size());
		for (MapStructure structure : chunk.getStructures()) {
			buffer.put(structure.getDefinition().getId());
			buffer.put((byte) (structure.getTileX() - chunk.getOffsetX()));
			buffer.put((byte) (structure.getTileY() - chunk.getOffsetY()));
		}
		data = Arrays.copyOf(buffer.array(), buffer.position());
	}

	public Chunk buildChunk(MapTile[] tiles, List<Structure> structures) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int x = buffer.getInt();
		int y = buffer.getInt();
		Chunk chunk = new Chunk(x, y);
		MapTile[] data = chunk.getTiles();
		int dataPosition = 0;
		while (dataPosition < GameConstants.CHUNK_SIZE * GameConstants.CHUNK_SIZE) {
			byte length = buffer.get();
			MapTile tile = tiles[buffer.get()];
			Arrays.fill(data, dataPosition, dataPosition + length, tile);
			dataPosition += length;
		}
		int structureCount = buffer.getShort();
		for (int i = 0; i < structureCount; i++) {
			chunk.addStructure(structures.get(buffer.get()), buffer.get() + chunk.getOffsetX(),
					buffer.get() + chunk.getOffsetY());
		}
		chunk.setCompressed(this);
		return chunk;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<CompressedChunk> {

		@Override
		public void write(Kryo kryo, Output output, CompressedChunk object) {
			output.writeInt(object.data.length);
			output.write(object.data);
		}

		@Override
		public CompressedChunk read(Kryo kryo, Input input, Class<CompressedChunk> type) {
			int length = input.readInt();
			return new CompressedChunk(input.readBytes(length));
		}

	}
}
