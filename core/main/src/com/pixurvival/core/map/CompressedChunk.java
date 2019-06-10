package com.pixurvival.core.map;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.util.ByteBufferUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class CompressedChunk {

	private TiledMap map;
	private @Getter byte[] data;

	public CompressedChunk(Chunk chunk) {
		map = chunk.getMap();
		ByteBuffer buffer = ByteBufferUtils.getThreadSafeInstance();
		buffer.reset();
		buffer.putInt(chunk.getPosition().getX());
		buffer.putInt(chunk.getPosition().getY());
		buffer.putLong(chunk.getUpdateTimestamp());
		MapTile currentTile = null;
		byte currentLength = 0;
		for (MapTile tile : chunk.getTiles()) {
			if (tile != currentTile || currentLength == Byte.MAX_VALUE) {
				if (currentTile != null) {
					buffer.put(currentLength);
					buffer.put((byte) currentTile.getTileDefinition().getId());
				}
				currentLength = 1;
				currentTile = tile;
			} else {
				currentLength++;
			}
		}
		buffer.put(currentLength);
		buffer.put((byte) currentTile.getTileDefinition().getId());
		buffer.putShort(chunk.getStructureCount());
		chunk.forEachStructure(structure -> {
			buffer.put((byte) structure.getDefinition().getId());
			buffer.put((byte) (structure.getTileX() - chunk.getOffsetX()));
			buffer.put((byte) (structure.getTileY() - chunk.getOffsetY()));
			structure.writeData(buffer);
		});
		data = Arrays.copyOf(buffer.array(), buffer.position());
	}

	public Chunk buildChunk() {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int x = buffer.getInt();
		int y = buffer.getInt();
		Chunk chunk = new Chunk(map, x, y);
		chunk.setUpdateTimestamp(buffer.getLong());
		MapTile[] chunkData = chunk.getTiles();
		int dataPosition = 0;
		while (dataPosition < GameConstants.CHUNK_SIZE * GameConstants.CHUNK_SIZE) {
			byte length = buffer.get();
			MapTile tile = map.getMapTilesById()[buffer.get()];
			Arrays.fill(chunkData, dataPosition, dataPosition + length, tile);
			dataPosition += length;
		}
		int structureCount = buffer.getShort();
		for (int i = 0; i < structureCount; i++) {
			MapStructure structure = chunk.addStructure(map.getWorld().getContentPack().getStructures().get(buffer.get()), buffer.get() + chunk.getOffsetX(), buffer.get() + chunk.getOffsetY(), false);
			structure.applyData(buffer);
		}
		chunk.setCompressed(this);
		return chunk;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<CompressedChunk> {

		@Override
		public void write(Kryo kryo, Output output, CompressedChunk object) {
			output.writeLong(object.map.getWorld().getId());
			output.writeInt(object.data.length);
			output.write(object.data);
		}

		@Override
		public CompressedChunk read(Kryo kryo, Input input, Class<CompressedChunk> type) {
			World world = World.getWorld(input.readLong());
			int length = input.readInt();
			return new CompressedChunk(world.getMap(), input.readBytes(length));
		}

	}
}
