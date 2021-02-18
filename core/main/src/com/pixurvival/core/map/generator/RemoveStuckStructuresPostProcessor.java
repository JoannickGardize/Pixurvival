package com.pixurvival.core.map.generator;

import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;

public class RemoveStuckStructuresPostProcessor implements ChunkPostProcessor {

	@Override
	public void apply(Chunk chunk) {
		List<StructureEntity> toRemove = new ArrayList<>();
		chunk.forEachStructure(mapStructure -> {
			if (!mapStructure.getDefinition().isAvoidStuck()) {
				return;
			}
			// TODO half remove of sides for other chunks ?
			for (int i = 0; i < mapStructure.getHeight(); i++) {
				if (Math.abs(mapStructure.getTileX() % 2) == 1 && (chunk.tileAt(mapStructure.getTileX() - 1, mapStructure.getTileY() + i).getStructure() != null
						|| (mapStructure.getTileX() - chunk.getOffsetX() < GameConstants.CHUNK_SIZE - mapStructure.getWidth()
								&& chunk.tileAt(mapStructure.getTileX() + mapStructure.getWidth(), mapStructure.getTileY() + i).getStructure() != null))) {
					toRemove.add(mapStructure);
					return;
				}
			}
			for (int i = 0; i < mapStructure.getWidth(); i++) {
				if (Math.abs(mapStructure.getTileY() % 2) == 1 && (chunk.tileAt(mapStructure.getTileX() + i, mapStructure.getTileY() - 1).getStructure() != null
						|| (mapStructure.getTileY() - chunk.getOffsetY() < GameConstants.CHUNK_SIZE - mapStructure.getHeight()
								&& chunk.tileAt(mapStructure.getTileX() + i, mapStructure.getTileY() + mapStructure.getHeight()).getStructure() != null))) {
					toRemove.add(mapStructure);
					return;
				}
			}
		});
		toRemove.forEach(m -> chunk.removeStructure(m.getTileX(), m.getTileY(), false));
	}
}
