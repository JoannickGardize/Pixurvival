package com.pixurvival.core.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.chunk.CompressedChunk;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import com.pixurvival.core.message.playerRequest.PlayerMovementRequest;
import com.pixurvival.core.util.KryoUtils;
import com.pixurvival.core.util.ObjectPools;
import com.pixurvival.core.util.Poolable;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WorldUpdate implements Poolable {

	public static final int BUFFER_SIZE = 16384;

	private @Setter long updateId;
	private @Setter long time;
	private @Setter int entityUpdateLength;
	private ByteBuffer entityUpdateByteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private List<StructureUpdate> structureUpdates = new ArrayList<>();
	private List<CompressedChunk> compressedChunks = new ArrayList<>();
	private long[] readyCooldowns = new long[4];
	private @Setter PlayerMovementRequest lastPlayerMovementRequest;

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
			output.writeLong(object.updateId);
			long time = object.time;
			output.writeLong(time);
			output.writeInt((int) (object.readyCooldowns[0] - time));
			output.writeInt((int) (object.readyCooldowns[1] - time));
			output.writeInt((int) (object.readyCooldowns[2] - time));
			output.writeInt((int) (object.readyCooldowns[3] - time));
			kryo.writeObject(output, object.lastPlayerMovementRequest);
			object.entityUpdateLength = object.entityUpdateByteBuffer.position();
			output.writeInt(object.entityUpdateLength);
			output.writeBytes(object.entityUpdateByteBuffer.array(), 0, object.entityUpdateByteBuffer.position());
			KryoUtils.writeUnspecifiedClassList(kryo, output, object.structureUpdates);
			KryoUtils.writeUniqueClassList(kryo, output, object.compressedChunks);
		}

		@Override
		public WorldUpdate read(Kryo kryo, Input input, Class<WorldUpdate> type) {
			WorldUpdate worldUpdate = ObjectPools.getWorldUpdatePool().get();
			worldUpdate.updateId = input.readLong();
			long time = input.readLong();
			worldUpdate.time = time;
			worldUpdate.readyCooldowns[0] = input.readInt() + time;
			worldUpdate.readyCooldowns[1] = input.readInt() + time;
			worldUpdate.readyCooldowns[2] = input.readInt() + time;
			worldUpdate.readyCooldowns[3] = input.readInt() + time;
			worldUpdate.lastPlayerMovementRequest = kryo.readObject(input, PlayerMovementRequest.class);
			worldUpdate.entityUpdateLength = input.readInt();
			input.readBytes(worldUpdate.entityUpdateByteBuffer.array(), 0, worldUpdate.entityUpdateLength);
			KryoUtils.readUnspecifiedClassList(kryo, input, worldUpdate.structureUpdates);
			KryoUtils.readUniqueClassList(kryo, input, worldUpdate.compressedChunks, CompressedChunk.class);
			return worldUpdate;
		}
	}
}
