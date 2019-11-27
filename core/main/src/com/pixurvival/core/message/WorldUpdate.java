package com.pixurvival.core.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.util.KryoUtils;
import com.pixurvival.core.util.ObjectPools;
import com.pixurvival.core.util.Poolable;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WorldUpdate implements Poolable {

	public static final int BUFFER_SIZE = 16384;

	private @Setter long updateId;
	private @Setter long worldId;
	private @Setter int entityUpdateLength;
	private ByteBuffer entityUpdateByteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private List<StructureUpdate> structureUpdates = new ArrayList<>();
	private List<CompressedChunk> compressedChunks = new ArrayList<>();

	@Override
	public void clear() {
		entityUpdateByteBuffer.position(0);
		structureUpdates.clear();
		compressedChunks.clear();
	}

	public boolean isEmpty() {
		return entityUpdateByteBuffer.position() <= 4 && structureUpdates.isEmpty() && compressedChunks.isEmpty();
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<WorldUpdate> {

		@Override
		public void write(Kryo kryo, Output output, WorldUpdate object) {
			output.writeLong(object.worldId);
			output.writeLong(object.updateId);
			object.entityUpdateLength = object.entityUpdateByteBuffer.position();
			output.writeInt(object.entityUpdateLength);
			output.writeBytes(object.entityUpdateByteBuffer.array(), 0, object.entityUpdateByteBuffer.position());
			KryoUtils.writeUnspecifiedClassList(kryo, output, object.structureUpdates);
			KryoUtils.writeUniqueClassList(kryo, output, object.compressedChunks);
		}

		@Override
		public WorldUpdate read(Kryo kryo, Input input, Class<WorldUpdate> type) {
			long worldId = input.readLong();
			World world = World.getWorld(worldId);
			if (world == null) {
				Log.error("EntitiesUpdate received for an unknown world : " + worldId);
				return null;
			}
			long updateId = input.readLong();
			WorldUpdate worldUpdate = ObjectPools.getWorldUpdatePool().get();
			worldUpdate.updateId = updateId;
			worldUpdate.entityUpdateLength = input.readInt();
			input.readBytes(worldUpdate.entityUpdateByteBuffer.array(), 0, worldUpdate.entityUpdateLength);
			KryoUtils.readUnspecifiedClassList(kryo, input, worldUpdate.structureUpdates);
			KryoUtils.readUniqueClassList(kryo, input, worldUpdate.compressedChunks, CompressedChunk.class);
			return worldUpdate;
		}
	}
}
