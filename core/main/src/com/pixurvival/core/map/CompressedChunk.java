package com.pixurvival.core.map;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

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
					// TODO TILE AND STRUCTURE
				}
				currentLength = 1;
				currentTile = tile;
			} else {
				currentLength++;
			}
		}
		if (currentLength != 0) {
			buffer.put(currentLength);
			buffer.put(currentTile.getTileDefinition().getId());
			currentLength = 0;
		}
		data = Arrays.copyOf(buffer.array(), buffer.position());
	}

	public Chunk buildChunk(List<MapTile> tiles) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int x = buffer.getInt();
		int y = buffer.getInt();
		Chunk chunk = new Chunk(x, y);
		MapTile[] data = chunk.getTiles();
		int dataPosition = 0;
		while (buffer.position() < buffer.capacity()) {
			byte length = buffer.get();
			MapTile tile = tiles.get(buffer.get());
			Arrays.fill(data, dataPosition, dataPosition + length, tile);
			dataPosition += length;
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
