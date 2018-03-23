package com.pixurvival.core.message;

import java.nio.ByteBuffer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.World;

import lombok.Getter;
import lombok.Setter;

@Getter
public class EntitiesUpdate {

	private @Setter long updateId = -1;
	private @Setter long worldId;
	private @Setter int length;
	private ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
	private @Setter HarvestableStructureUpdate[] structureUpdates;

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<EntitiesUpdate> {

		@Override
		public void write(Kryo kryo, Output output, EntitiesUpdate object) {
			output.writeLong(object.updateId);
			output.writeLong(object.worldId);
			output.writeInt(object.byteBuffer.position());
			output.writeBytes(object.byteBuffer.array(), 0, object.byteBuffer.position());
			if (object.structureUpdates == null) {
				output.writeShort(-1);
			} else {
				output.writeShort(object.structureUpdates.length);
				for (int i = 0; i < object.structureUpdates.length; i++) {
					kryo.writeObject(output, object.structureUpdates[i]);
				}
			}
		}

		@Override
		public EntitiesUpdate read(Kryo kryo, Input input, Class<EntitiesUpdate> type) {
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
				EntitiesUpdate entitiesUpdate = world.getEntitiesUpdate();
				if (entitiesUpdate.updateId >= updateId) {
					int length = input.readInt();
					input.setPosition(input.position() + length);
					return entitiesUpdate;
				}
				entitiesUpdate.updateId = updateId;
				entitiesUpdate.length = input.readInt();
				input.readBytes(entitiesUpdate.byteBuffer.array(), 0, entitiesUpdate.length);
				entitiesUpdate.structureUpdates = readStructureUpdates(kryo, input);
				return entitiesUpdate;
			}
		}

		private HarvestableStructureUpdate[] readStructureUpdates(Kryo kryo, Input input) {
			short length = input.readShort();
			if (length == -1) {
				return null;
			}
			HarvestableStructureUpdate[] result = new HarvestableStructureUpdate[length];
			for (int i = 0; i < length; i++) {
				HarvestableStructureUpdate hs = kryo.readObject(input, HarvestableStructureUpdate.class);
				result[i] = hs;
			}
			return result;
		}
	}
}
