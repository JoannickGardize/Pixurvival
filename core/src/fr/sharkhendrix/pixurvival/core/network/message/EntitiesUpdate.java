package fr.sharkhendrix.pixurvival.core.network.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import fr.sharkhendrix.pixurvival.core.World;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EntitiesUpdate {

	private @Setter long updateId = -1;
	private @Setter long worldId;
	private @Setter int length;
	private byte[] bytes = new byte[4096];
	private Output lastOutput;

	public Input getInput() {
		return new Input(bytes, 0, length);
	}

	public Output getOutput() {
		lastOutput = new Output(bytes);
		return lastOutput;
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<EntitiesUpdate> {

		@Override
		public void write(Kryo kryo, Output output, EntitiesUpdate object) {
			output.writeLong(object.updateId);
			output.writeLong(object.worldId);
			output.writeInt(object.lastOutput.position());
			output.writeBytes(object.bytes);
		}

		@Override
		public EntitiesUpdate read(Kryo kryo, Input input, Class<EntitiesUpdate> type) {
			long updateId = input.readLong();
			long worldId = input.readLong();
			World world = World.getWorld(worldId);
			synchronized (world) {
				if (world == null || world.getEntitiesUpdate().updateId >= updateId) {
					return null;
				}
				EntitiesUpdate entitiesUpdate = world.getEntitiesUpdate();
				entitiesUpdate.updateId = updateId;
				entitiesUpdate.length = input.readInt();
				input.readBytes(entitiesUpdate.bytes, 0, entitiesUpdate.length);
				return entitiesUpdate;
			}
		}
	}
}
