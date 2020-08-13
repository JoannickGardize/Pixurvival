package com.pixurvival.core.map;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RemoveDurationStructureAction implements Action {

	private int x;
	private int y;

	@Override
	public void perform(World world) {
		MapTile tile = world.getMap().tileAt(x, y);
		if (tile instanceof TileAndStructure) {
			MapStructure mapStructure = ((TileAndStructure) tile).getStructure();
			long duration = mapStructure.getDefinition().getDuration();
			if (duration > 0 && world.getTime().getTimeMillis() - mapStructure.getCreationTime() >= duration) {
				mapStructure.getChunk().removeStructure(x, y);
			}
		}
	}

}
