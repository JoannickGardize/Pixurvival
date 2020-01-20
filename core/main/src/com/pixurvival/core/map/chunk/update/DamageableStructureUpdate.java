package com.pixurvival.core.map.chunk.update;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.map.DamageableMapStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.chunk.Chunk;

public class DamageableStructureUpdate extends StructureUpdate {

	private float health;

	public DamageableStructureUpdate(int x, int y, float health) {
		super(x, y);
		this.health = health;
	}

	@Override
	public void apply(Chunk chunk) {
		MapTile mapTile = chunk.tileAt(getX(), getY());
		if (mapTile.getStructure() instanceof DamageableMapStructure) {
			DamageableMapStructure structure = (DamageableMapStructure) mapTile.getStructure();
			structure.setHealth(health);
			chunk.getMap().notifyListeners(l -> l.structureChanged(structure));
		}
		chunk.invalidateCompressed();
	}

	public static class Serializer extends com.esotericsoftware.kryo.Serializer<DamageableStructureUpdate> {

		@Override
		public void write(Kryo kryo, Output output, DamageableStructureUpdate object) {
			output.writeInt(object.getX());
			output.writeInt(object.getY());
			output.writeFloat(object.health);
		}

		@Override
		public DamageableStructureUpdate read(Kryo kryo, Input input, Class<DamageableStructureUpdate> type) {
			return new DamageableStructureUpdate(input.readInt(), input.readInt(), input.readFloat());
		}
	}
}
