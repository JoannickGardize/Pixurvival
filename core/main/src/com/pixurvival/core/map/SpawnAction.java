package com.pixurvival.core.map;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ecosystem.ChunkSpawner;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class SpawnAction implements Action {

	private ChunkPosition chunkPosition;
	private ChunkSpawner spawner;

	@Override
	public void perform(World world) {
		Chunk chunk = world.getMap().chunkAt(chunkPosition);
		if (chunk != null && spawner.isChunkEligible(chunk)) {
			spawner.spawn(chunk);
			world.getActionTimerManager().addActionTimer(new SpawnAction(chunkPosition, spawner), spawner.getRespawnTime().next(world.getRandom()));
		} else {
			world.getChunkCreatureSpawnManager().removeChunkSpawnerMemory(chunkPosition, spawner);
		}
	}
}
