package com.pixurvival.core.contentPack.ecosystem;

import java.util.List;

import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.util.CollectionUtils;
import com.pixurvival.core.util.IntWrapper;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.core.util.WorldRandom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructureSpawner extends ChunkSpawner {

	private static final long serialVersionUID = 1L;

	@ElementReference
	private Structure structure;

	@Positive
	private float spawnRadius;

	@Positive
	private float managedRadius;

	@Override
	protected Object beginSpawn(Chunk chunk) {
		List<StructureEntity> structures = chunk.getStructures(structure.getId());
		if (structures.isEmpty()) {
			return null;
		}
		WorldRandom random = chunk.getMap().getWorld().getRandom();
		return structures.get(random.nextInt(structures.size())).getPosition();
	}

	@Override
	public boolean isChunkEligible(Chunk chunk) {
		return !CollectionUtils.isNullOrEmpty(chunk.getStructures(structure.getId()));
	}

	@Override
	protected Vector2 nextSpawnPosition(Chunk chunk, Object data) {
		WorldRandom random = chunk.getMap().getWorld().getRandom();
		return random.nextVector2InCircle((Vector2) data, spawnRadius);
	}

	@Override
	protected int countCreatures(Chunk chunk, Object data) {
		IntWrapper counter = new IntWrapper();
		chunk.getMap().forEachEntities(EntityGroup.CREATURE, (Vector2) data, managedRadius, entity -> {
			if (getCreatureSet().contains(((CreatureEntity) entity).getDefinition())) {
				counter.increment();
			}
		});
		return counter.getValue();
	}
}
