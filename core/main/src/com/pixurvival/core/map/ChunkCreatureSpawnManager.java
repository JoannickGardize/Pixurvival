package com.pixurvival.core.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.pixurvival.core.Action;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.ecosystem.ChunkSpawner;
import com.pixurvival.core.contentPack.ecosystem.DarknessSpawner;
import com.pixurvival.core.contentPack.ecosystem.Ecosystem;
import com.pixurvival.core.contentPack.ecosystem.StructureSpawner;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.map.chunk.ChunkPosition;

import lombok.AllArgsConstructor;

public class ChunkCreatureSpawnManager implements TiledMapListener {

	@AllArgsConstructor
	private class SpawnAction implements Action {

		private World world;
		private ChunkPosition chunkPosition;
		private ChunkSpawner spawner;

		@Override
		public void perform() {
			Chunk chunk = world.getMap().chunkAt(chunkPosition);
			if (chunk != null && spawner.isChunkEligible(chunk)) {
				spawner.spawn(chunk);
				world.getActionTimerManager().addActionTimer(new SpawnAction(world, chunkPosition, spawner), spawner.getRespawnTime().next(world.getRandom()));
			} else {
				Set<ChunkSpawner> spawnerSet = actionMemory.get(chunkPosition);
				if (spawnerSet != null) {
					spawnerSet.remove(spawner);
					if (spawnerSet.isEmpty()) {
						actionMemory.remove(chunkPosition);
					}
				}
			}
		}
	}

	private Map<ChunkPosition, Set<ChunkSpawner>> actionMemory = new HashMap<>();

	@Override
	public void chunkLoaded(Chunk chunk) {
		ChunkSpawner darknessSpawner = chunk.getMap().getWorld().getGameMode().getEcosystem().getDarknessSpawner();
		if (chunk.isNewlyCreated()) {
			forEachStructureSpawner(chunk, spawner -> spawn(chunk, spawner));
			spawn(chunk, darknessSpawner);
		} else {
			forEachStructureSpawner(chunk, spawner -> respawn(chunk, spawner));
			respawn(chunk, darknessSpawner);
		}
	}

	private void spawn(Chunk chunk, ChunkSpawner spawner) {
		for (int i = 0; i < spawner.getInitialSpawn(); i++) {
			spawner.spawn(chunk);
		}
		addSpawnerActionTimer(chunk, spawner);
	}

	private void respawn(Chunk chunk, ChunkSpawner spawner) {
		Set<ChunkSpawner> spawnerSet = actionMemory.get(chunk.getPosition());
		if (spawnerSet == null || !spawnerSet.contains(spawner)) {
			spawner.spawn(chunk);
			addSpawnerActionTimer(chunk, spawner);
		}
	}

	private void addSpawnerActionTimer(Chunk chunk, ChunkSpawner spawner) {
		World world = chunk.getMap().getWorld();
		if (spawner instanceof DarknessSpawner) {
		}
		world.getActionTimerManager().addActionTimer(new SpawnAction(world, chunk.getPosition(), spawner), spawner.getRespawnTime().next(world.getRandom()));
		actionMemory.computeIfAbsent(chunk.getPosition(), p -> new HashSet<>()).add(spawner);
	}

	private void forEachStructureSpawner(Chunk chunk, Consumer<StructureSpawner> action) {
		World world = chunk.getMap().getWorld();
		Ecosystem ecosystem = world.getGameMode().getEcosystem();
		for (Entry<Integer, List<MapStructure>> entry : chunk.getStructures()) {
			List<StructureSpawner> structureSpawners = ecosystem.getStructureSpawnersPerStructure().get(entry.getKey());
			if (structureSpawners == null || structureSpawners.isEmpty()) {
				continue;
			}
			for (StructureSpawner structureSpawner : structureSpawners) {
				action.accept(structureSpawner);
			}
		}
	}

	@Override
	public void chunkUnloaded(Chunk chunk) {
	}

	@Override
	public void structureChanged(MapStructure mapStructure) {
	}

	@Override
	public void structureAdded(MapStructure mapStructure) {
		// TODO reactiver le spawner quand un nouveau type de structure apparait
		// dans le chunk
	}

	@Override
	public void structureRemoved(MapStructure mapStructure) {
	}

	@Override
	public void entityEnterChunk(ChunkPosition previousPosition, Entity e) {
	}

}