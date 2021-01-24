package com.pixurvival.core.contentPack.ecosystem;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.TimeInterval;
import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.core.util.WorldRandom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ChunkSpawner implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final Object ENPTY_DATA = new Object();

	@Valid
	private WeightedValueProducer<Creature> creatureChooser = new WeightedValueProducer<>();

	@Positive
	private int initialSpawn;

	@Positive
	private int maximumCreatures;

	@Valid
	private TimeInterval respawnTime = new TimeInterval();

	private transient Set<Creature> creatureSet;

	/**
	 * Unique id in Ecosystem
	 */
	private transient int id;

	public void spawn(Chunk chunk) {
		Object data = beginSpawn(chunk);
		if (data == null || countCreatures(chunk, data) >= maximumCreatures || creatureChooser.isEmpty()) {
			return;
		}
		Vector2 randomPosition = nextSpawnPosition(chunk, data);
		WorldRandom random = chunk.getMap().getWorld().getRandom();
		Creature creature = creatureChooser.next(random);
		if (isSpawnValid(chunk, creature, randomPosition)) {
			CreatureEntity entity = new CreatureEntity(creature);
			entity.getPosition().set(randomPosition);
			chunk.getMap().getWorld().getEntityPool().addNew(entity);
		}
	}

	public void buildCreatureSet() {
		creatureSet = new HashSet<>();
		creatureChooser.getBackingArray().forEach(entry -> creatureSet.add(entry.getElement()));
	}

	/**
	 * For override
	 * 
	 * @param chunk
	 *            The chunk
	 * @return true if the chunk is eligible for this spawner.
	 */
	public boolean isChunkEligible(Chunk chunk) {
		return true;
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

	@AllArgsConstructor
	public static class Serializer extends com.esotericsoftware.kryo.Serializer<ChunkSpawner> {

		// TODO use WorldKryo#getWorld
		private Ecosystem ecosystem;

		@Override
		public void write(Kryo kryo, Output output, ChunkSpawner object) {
			output.writeInt(object.getId());
		}

		@Override
		public ChunkSpawner read(Kryo kryo, Input input, Class<ChunkSpawner> type) {
			return ecosystem.getChunkSpawnerById(input.readInt());
		}
	}
}
