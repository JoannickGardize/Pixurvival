package com.pixurvival.core.map.chunk.update;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.chunk.Chunk;

public class DamageableStructureUpdate extends StructureUpdate {

	private float health;

	public DamageableStructureUpdate(int x, int y, long id, float health) {
		super(x, y, id);
		this.health = health;
	}

	@Override
	public void apply(Chunk chunk) {
		MapTile mapTile = chunk.tileAt(getX(), getY());
		MapStructure structure = mapTile.getStructure();
		if (structure != null && structure.getId() == getId()) {
			structure.setHealth(health);
			chunk.getMap().notifyListeners(l -> l.structureChanged(structure, this));
		}
		chunk.invalidateCompressed();
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<DamageableStructureUpdate> {

		@Override
		public void write(Kryo kryo, Output output, DamageableStructureUpdate object) {
			output.writeInt(object.getX());
			output.writeInt(object.getY());
			output.writeVarLong(object.getId(), true);
			output.writeFloat(object.health);
		}

		@Override
		public DamageableStructureUpdate read(Kryo kryo, Input input, Class<DamageableStructureUpdate> type) {
			return new DamageableStructureUpdate(input.readInt(), input.readInt(), input.readVarLong(true), input.readFloat());
		}
	}
}
