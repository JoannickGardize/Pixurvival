package com.pixurvival.core.map;

import java.nio.ByteBuffer;

import com.pixurvival.core.Time;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.structure.ShortLivedStructure;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.message.StructureUpdate;

public class ShortLivedMapStructure extends MapStructure {

	public ShortLivedMapStructure(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
		World world = chunk.getMap().getWorld();

		if (world.isServer()) {
			world.getActionTimerManager().addActionTimer(() -> {
				MapTile tile = chunk.tileAt(x, y);
				if (tile instanceof TileAndStructure && ((TileAndStructure) tile).getStructure() == ShortLivedMapStructure.this) {
					chunk.removeStructure(x, y);
				}
			}, Time.secToMillis(((ShortLivedStructure) definition).getDuration()));
		}
	}

	@Override
	public void writeData(ByteBuffer buffer) {
	}

	@Override
	public void applyData(ByteBuffer buffer) {
	}

	@Override
	public StructureUpdate getUpdate() {
		return null;
	}

}
