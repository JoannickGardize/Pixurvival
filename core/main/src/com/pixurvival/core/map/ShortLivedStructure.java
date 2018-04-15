package com.pixurvival.core.map;

import com.pixurvival.core.ActionTimer;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.Structure;

public class ShortLivedStructure extends MapStructure {

	public ShortLivedStructure(Chunk chunk, Structure definition, int x, int y) {
		super(chunk, definition, x, y);
		World world = chunk.getMap().getWorld();

		if (world.isServer()) {
			world.getActionTimerManager().add(new ActionTimer(() -> {
				MapTile tile = chunk.tileAt(x, y);
				if (tile instanceof TileAndStructure
						&& ((TileAndStructure) tile).getStructure() == ShortLivedStructure.this) {
					chunk.removeStructure(x, y);
				}
			}, world.getTime().getTime() + definition.getDuration()));
		}
	}

}
