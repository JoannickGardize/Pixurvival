package com.pixurvival.core.map.chunk.update;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;

@Getter
public class HarvestableStructureUpdate extends StructureUpdate {

	private boolean harvested;

	public HarvestableStructureUpdate(int x, int y, boolean harvested) {
		super(x, y);
		this.harvested = harvested;
	}

	@Override
	public void apply(Chunk chunk) {
		MapTile mapTile = chunk.tileAt(getX(), getY());
		if (mapTile.getStructure() instanceof HarvestableMapStructure) {
			((HarvestableMapStructure) mapTile.getStructure()).setHarvested(harvested);
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