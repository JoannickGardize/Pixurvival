package com.pixurvival.core.contentPack.ecosystem;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.LongInterval;
import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.ChunkPosition;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.core.util.WorldRandom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChunkSpawner implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Object ENPTY_DATA = new Object();

	private WeightedValueProducer<Creature> creatureChooser = new WeightedValueProducer<>();

	private int initialSpawn;

	private int maximumCreatures;

	private LongInterval respawnTime;

	private transient Set<Creature> creatureSet;

	public void spawn(Chunk chunk) {
		Object data = beginSpawn(chunk);
		if (countCreatures(chunk, data) >= maximumCreatures) {
			return;
		}
		Vector2 randomPosition = nextSpawnPosition(chunk, data);
		WorldRandom random = chunk.getMap().getWorld().getRandom();
		Creature creature = creatureChooser.next(random);
		if (isSpawnValid(chunk, creature, randomPosition)) {
			CreatureEntity entity = new CreatureEntity(creature);
			entity.getPosition().set(randomPosition);
			chunk.getMap().getWorld().getEntityPool().add(entity);
		}
	}

	public void buildCreatureSet() {
		creatureSet = new HashSet<>();
		creatureChooser.getBackingArray().forEach(entry -> creatureSet.add(entry.getElement()));
	}

	public void addActionTimer(World world, ChunkPosition chunkPosition) {

	}

	/**
	 * Initialize data for a creature spawning
	 * 
	 * @param chunk
	 *            The chunk where spawn must be done
	 * @return the data for the spawning, or null to abort spawning
	 */
	protected Object beginSpawn(Chunk chunk) {
		return ENPTY_DATA;
	}

	/**
	 * @param chunk
	 * @param data
	 *            Data initialized with {@link ChunkSpawner#beginSpawn(Chunk)}
	 * @return
	 */
	protected Vector2 nextSpawnPosition(Chunk chunk, Object data) {
		WorldRandom random = chunk.getMap().getWorld().getRandom();
		return random.nextVector2InRectangle(chunk.getOffsetX(), chunk.getOffsetY(), GameConstants.CHUNK_SIZE, GameConstants.CHUNK_SIZE);
	}

	/**
	 * @param chunk
	 * @param data
	 *            Data initialized with {@link ChunkSpawner#beginSpawn(Chunk)}
	 * @return
	 */
	protected int countCreatures(Chunk chunk, Object data) {
		int count = 0;
		for (Entity entity : chunk.getEntities().get(EntityGroup.CREATURE)) {
			if (creatureSet.contains(((CreatureEntity) entity).getDefinition())) {
				count++;
			}
		}
		return count;
	}

	protected boolean isSpawnValid(Chunk chunk, Creature creature, Vector2 position) {
		return creature != null && (!creature.isSolid() || !chunk.getMap().collide(position.getX(), position.getY(), creature.getCollisionRadius()));
	}

}
