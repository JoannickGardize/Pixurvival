package com.pixurvival.core.message;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.SyncWorldUpdate;
import com.pixurvival.core.World;
import com.pixurvival.core.map.CompressedChunk;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WorldUpdate {

	public static final int BUFFER_SIZE = 4096;

	private @Setter long updateId = -1;
	private @Setter long worldId;
	private @Setter int length;
	private ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
	private @Setter StructureUpdate[] structureUpdates;
	private @Setter PlayerData[] playerData;
	private @Setter CompressedChunk[] compressedChunks;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<WorldUpdate> {

		@Override
		public void write(Kryo kryo, Output output, WorldUpdate object) {
			output.writeLong(object.updateId);
			output.writeLong(object.worldId);
			output.writeInt(object.byteBuffer.position());
			output.writeBytes(object.byteBuffer.array(), 0, object.byteBuffer.position());
			if (object.structureUpdates == null) {
				output.writeShort(-1);
			} else {
				output.writeShort(object.structureUpdates.length);
				for (int i = 0; i < object.structureUpdates.length; i++) {
					kryo.writeClassAndObject(output, object.structureUpdates[i]);
				}
			}
			if (object.playerData == null) {
				output.writeShort(-1);
			} else {
				output.writeShort(object.playerData.length);
				for (int i = 0; i < object.playerData.length; i++) {
					kryo.writeObject(output, object.playerData[i]);
				}
			}
			kryo.writeObjectOrNull(output, object.compressedChunks, CompressedChunk[].class);
		}

		@Override
		public WorldUpdate read(Kryo kryo, Input input, Class<WorldUpdate> type) {
			long updateId = input.readLong();
			long worldId = input.readLong();
			World world = World.getWorld(worldId);
			if (world == null) {
				int length = input.readInt();
				input.setPosition(input.position() + length);
				readStructureUpdates(kryo, input);
				Log.error("EntitiesUpdate received for an unknown world : " + worldId);
				return null;
			}
			synchronized (world) {
				WorldUpdate entitiesUpdate = world.getWorldUpdate();
				if (entitiesUpdate.updateId >= updateId) {
					int length = input.readInt();
					input.setPosition(input.position() + length);
					return entitiesUpdate;
				}
				entitiesUpdate.updateId = updateId;
				entitiesUpdate.length = input.readInt();
				input.readBytes(entitiesUpdate.byteBuffer.array(), 0, entitiesUpdate.length);
				StructureUpdate[] structureUpdates = readStructureUpdates(kryo, input);
				PlayerData[] playerData = readPlayerData(kryo, input);
				CompressedChunk[] compressedChunks = kryo.readObjectOrNull(input, CompressedChunk[].class);
				SyncWorldUpdate syncWorldUpdate = new SyncWorldUpdate();
				syncWorldUpdate.setStructureUpdates(structureUpdates);
				syncWorldUpdate.setPlayerData(playerData);
				syncWorldUpdate.setCompressedChunks(compressedChunks);
				syncWorldUpdate.setUpdateId(updateId);
				world.getSyncWorldUpdateManager().add(syncWorldUpdate);
				return entitiesUpdate;
			}
		}

		private StructureUpdate[] readStructureUpdates(Kryo kryo, Input input) {
			short length = input.readShort();
			if (length == -1) {
				return null;
			}
			StructureUpdate[] result = new StructureUpdate[length];
			for (int i = 0; i < length; i++) {
				StructureUpdate hs = (StructureUpdate) kryo.readClassAndObject(input);
				result[i] = hs;
			}
			return result;
		}

		private PlayerData[] readPlayerData(Kryo kryo, Input input) {
			short length = input.readShort();
			if (length == -1) {
				return null;
			}
			PlayerData[] result = new PlayerData[length];
			for (int i = 0; i < length; i++) {
				result[i] = kryo.readObject(input, PlayerData.class);
			}
			return result;
		}
	}
}
