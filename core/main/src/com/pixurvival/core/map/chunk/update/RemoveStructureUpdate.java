package com.pixurvival.core.map.chunk.update;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.chunk.Chunk;

public class RemoveStructureUpdate extends StructureUpdate {

	public RemoveStructureUpdate(int x, int y) {
		super(x, y);
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<RemoveStructureUpdate> {

		@Override
		public void write(Kryo kryo, Output output, RemoveStructureUpdate object) {
			output.writeInt(object.getX());
			output.writeInt(object.getY());
		}

		@Override
		public RemoveStructureUpdate read(Kryo kryo, Input input, Class<RemoveStructureUpdate> type) {
			return new RemoveStructureUpdate(input.readInt(), input.readInt());
		}
	}

	@Override
	public void apply(Chunk chunk) {
		chunk.removeStructure(getX(), getY());
		chunk.invalidateCompressed();
	}
}