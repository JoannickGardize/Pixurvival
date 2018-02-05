package fr.sharkhendrix.pixurvival.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * This class contains all entities of a given {@link World}, packed by group
 * defined in enum {@link EntityGroup}.
 * 
 * @author SharkHendirx
 *
 */
public class EntityPool {
	private World world;
	private Map<EntityGroup, Map<Long, Entity>> entities = new HashMap<>();
	private long nextId = 0;

	public EntityPool(World world) {
		this.world = world;
		for (EntityGroup group : EntityGroup.values()) {
			entities.put(group, new HashMap<>());
		}
	}

	public void add(Entity e) {
		e.setWorld(world);
		if (world.isServer()) {
			e.setId(nextId++);
		}
		entities.get(e.getGroup()).put(e.getId(), e);
	}

	public void remove(Entity e) {
		entities.get(e.getGroup()).remove(e.getId());
	}

	public void update() {
		for (Map<Long, Entity> groupMap : entities.values()) {
			Collection<Entity> groupCollection = groupMap.values();
			groupCollection.removeIf(e -> !e.isAlive());
			groupCollection.forEach(e -> e.update());
		}
	}

	public Entity get(EntityGroup group, long id) {
		return entities.get(group).get(id);
	}

	public Collection<Entity> get(EntityGroup group) {
		return entities.get(group).values();
	}

	public void foreach(Consumer<Entity> action) {
		entities.values().forEach(m -> m.values().forEach(action));
	}

	public void writeUpdate(Output output) {
		output.writeByte((byte) entities.size());
		for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
			output.writeByte(groupEntry.getKey().getId());
			Map<Long, Entity> entityMap = groupEntry.getValue();
			output.writeInt(entityMap.size());
			for (Entity e : entityMap.values()) {
				output.writeLong(e.getId());
				output.writeByte(EntityRegistry.getIdOf(e.getClass()));
				e.writeUpdate(output);
			}
		}

	}

	public void applyUpdate(Input input) {
		byte groupCount = input.readByte();
		for (int i = 0; i < groupCount; i++) {
			EntityGroup group = EntityGroup.values()[input.readByte()];
			Map<Long, Entity> groupMap = entities.get(group);
			groupMap.values().forEach(e -> e.setAlive(false));
			int groupSize = input.readInt();
			for (int j = 0; j < groupSize; j++) {
				long entityId = input.readLong();
				byte classId = input.readByte();
				Entity e = groupMap.get(entityId);
				if (e == null) {
					e = EntityRegistry.newEntity(classId);
					e.setId(entityId);
					add(e);
				}
				e.setAlive(true);
				e.applyUpdate(input);
			}
		}
	}
}
