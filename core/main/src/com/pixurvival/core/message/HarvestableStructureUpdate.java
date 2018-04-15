package com.pixurvival.core.message;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapTile;

import lombok.Getter;

@Getter
public class HarvestableStructureUpdate extends StructureUpdate {

	private boolean harvested;

	public HarvestableStructureUpdate(int x, int y, boolean harvested) {
		super(x, y);
		this.harvested = harvested;
	}

	@Override
	public void perform(Chunk chunk) {
		MapTile mapTile = chunk.tileAt(getX(), getY());
		if (mapTile.getStructure() instanceof HarvestableStructure) {
			((HarvestableStructure) mapTile.getStructure()).setHarvested(harvested);
		}
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<HarvestableStructureUpdate> {

		@Override
		public void write(Kryo kryo, Output output, HarvestableStructureUpdate object) {
			output.writeInt(object.getX());
			output.writeInt(object.getY());
			output.writeBoolean(object.harvested);
		}

		@Override
		public HarvestableStructureUpdate read(Kryo kryo, Input input, Class<HarvestableStructureUpdate> type) {
			return new HarvestableStructureUpdate(input.readInt(), input.readInt(), input.readBoolean());
		}
	}
}
