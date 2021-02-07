package com.pixurvival.core.map.chunk.update;

import java.util.Collections;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.chunk.Chunk;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HarvestableStructureUpdate extends StructureUpdate implements Action {

	private boolean harvested;

	public HarvestableStructureUpdate(int x, int y, long id, boolean harvested) {
		super(x, y, id);
		this.harvested = harvested;
	}

	@Override
	public void apply(Chunk chunk) {
		MapTile mapTile = chunk.tileAt(getX(), getY());
		MapStructure structure = mapTile.getStructure();
		if (structure != null && structure.getId() == getId()) {
			HarvestableMapStructure hms = (HarvestableMapStructure) mapTile.getStructure();
			hms.setHarvested(harvested);
			chunk.getMap().notifyListeners(l -> l.structureChanged(hms, this));
		}
		chunk.invalidateCompressed();
	}

	@Override
	public void perform(World world) {
		world.getMap().applyUpdate(Collections.singleton(this));
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<HarvestableStructureUpdate> {

		@Override
		public void write(Kryo kryo, Output output, HarvestableStructureUpdate object) {
			output.writeInt(object.getX());
			output.writeInt(object.getY());
			output.writeVarLong(object.getId(), true);
			output.writeBoolean(object.harvested);
		}

		@Override
		public HarvestableStructureUpdate read(Kryo kryo, Input input, Class<HarvestableStructureUpdate> type) {
			return new HarvestableStructureUpdate(input.readInt(), input.readInt(), input.readVarLong(true), input.readBoolean());
		}
	}

}
