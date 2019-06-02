package com.pixurvival.core.contentPack.ecosystem;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.util.IntWrapper;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.core.util.WorldRandom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructureSpawner implements Serializable {

	private static final long serialVersionUID = 1L;

	private Structure structure;

	private WeightedValueProducer<Creature> creatureChooser = new WeightedValueProducer<>();

	private double spawnRadius;

	private double managedRadius;

	private int initialSpawnPerChunk;

	private int maximumCreatures;

	private double respawnTimePerChunk;

	private transient Set<Creature> creatureSet;

	public void spawn(Chunk chunk) {
		ensureCreatureSetBuilt();
		List<MapStructure> structures = chunk.getStructures().get(structure.getId());
		if (structures == null || structures.isEmpty()) {
			return;
		}
		WorldRandom random = chunk.getMap().getWorld().getRandom();
		Vector2 position = structures.get(random.nextInt(structures.size())).getPosition();
		IntWrapper counter = new IntWrapper();
		chunk.getMap().forEachEntities(EntityGroup.CREATURE, position, managedRadius, entity -> {
			if (creatureSet.contains(((CreatureEntity) entity).getDefinition())) {
				counter.increment();
			}
		});
		if (counter.getValue() >= maximumCreatures) {
			return;
		}
		Vector2 randomPosition = Vector2.fromEuclidean(random.nextDouble() * spawnRadius, random.nextAngle()).add(position);
		Creature creature = creatureChooser.next(random);
		if (creature == null || chunk.getMap().collide(randomPosition.getX(), randomPosition.getY(), creature.getCollisionRadius())) {
			return;
		}
		CreatureEntity entity = new CreatureEntity(creature);
		entity.getPosition().set(randomPosition);
		chunk.getMap().getWorld().getEntityPool().add(entity);
	}

	private void ensureCreatureSetBuilt() {
		if (creatureSet == null) {
			creatureSet = new HashSet<>();
			creatureChooser.getBackingArray().forEach(entry -> creatureSet.add(entry.getElement()));
		}
	}

}
