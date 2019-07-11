package com.pixurvival.core.contentPack.ecosystem;

import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.util.Vector2;

public class DarknessSpawner extends ChunkSpawner {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean isSpawnValid(Chunk chunk, Creature creature, Vector2 position) {
		boolean isDay = chunk.getMap().getWorld().getTime().getDayCycle().isDay();
		boolean result = !isDay && super.isSpawnValid(chunk, creature, position) && !chunk.getMap().isInAnyLight(position);
		return result;
	}

}
