package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.map.Chunk;

import lombok.Getter;

@Getter
public class AddStructureUpdate extends StructureUpdate {

	private byte structureId;

	public AddStructureUpdate(int x, int y, byte structureId) {
		super(x, y);
		this.structureId = structureId;
	}

	@Override
	public void perform(Chunk chunk) {
		Structure structure = chunk.getMap().getWorld().getContentPack().getStructures().get(structureId);
		if (chunk.isEmpty(getX(), getY(), structure.getDimensions().getWidth(),
				structure.getDimensions().getHeight())) {
			chunk.addStructure(structure, getX(), getY());
		}
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<AddStructureUpdate> {

		@Override
		public void write(Kryo kryo, Output output, AddStructureUpdate object) {
			output.writeInt(object.getX());
			output.writeInt(object.getY());
			output.writeByte(object.structureId);
		}

		@Override
		public AddStructureUpdate read(Kryo kryo, Input input, Class<AddStructureUpdate> type) {
			return new AddStructureUpdate(input.readInt(), input.readInt(), input.readByte());
		}
	}

}
