package com.pixurvival.core.map.chunk.update;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;

@Getter
public class AddStructureUpdate extends StructureUpdate {

	private int structureId;
	private long creationTime;

	public AddStructureUpdate(int x, int y, int structureId, long creationTime) {
		super(x, y);
		this.structureId = structureId;
		this.creationTime = creationTime;
	}

	@Override
	public void apply(Chunk chunk) {
		Structure structure = chunk.getMap().getWorld().getContentPack().getStructures().get(structureId);
		if (chunk.isEmpty(getX(), getY(), structure.getDimensions().getWidth(), structure.getDimensions().getHeight())) {
			chunk.addStructure(structure, getX(), getY()).setCreationTime(creationTime);
		}
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<AddStructureUpdate> {

		@Override
		public void write(Kryo kryo, Output output, AddStructureUpdate object) {
			output.writeInt(object.getX());
			output.writeInt(object.getY());
			output.writeInt(object.structureId);
			output.writeLong(object.creationTime);
		}

		@Override
		public AddStructureUpdate read(Kryo kryo, Input input, Class<AddStructureUpdate> type) {
			return new AddStructureUpdate(input.readInt(), input.readInt(), input.readInt(), input.readLong());
		}
	}

}
