package com.pixurvival.core.map;

import java.nio.ByteBuffer;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Structure.ShortLived;
import com.pixurvival.core.message.StructureUpdate;

public class ShortLivedStructure extends MapStructure {

	public ShortLivedStructure(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
		World world = chunk.getMap().getWorld();

		if (world.isServer()) {
			world.getActionTimerManager().addActionTimer(() -> {
				MapTile tile = chunk.tileAt(x, y);
				if (tile instanceof TileAndStructure
						&& ((TileAndStructure) tile).getStructure() == ShortLivedStructure.this) {
					chunk.removeStructure(x, y);
				}
			}, ((ShortLived) definition.getDetails()).getDuration());
		}
	}

	@Override
	public void writeData(ByteBuffer buffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyData(ByteBuffer buffer) {
		// TODO Auto-generated method stub
	}

	@Override
	public StructureUpdate getUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

}
