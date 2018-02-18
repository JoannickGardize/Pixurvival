package com.pixurvival.core;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

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
	private List<EntityPoolListener> listeners = new ArrayList<>();

	public EntityPool(World world) {
		this.world = world;
		for (EntityGroup group : EntityGroup.values()) {
			entities.put(group, new HashMap<>());
		}
	}

	public void addListener(EntityPoolListener l) {
		listeners.add(l);
	}

	public void add(Entity e) {
		e.setWorld(world);
		if (world.isServer()) {
			e.setId(nextId++);
		}
		entities.get(e.getGroup()).put(e.getId(), e);
		listeners.forEach(l -> l.entityAdded(e));
	}

	public void update() {
		for (Map<Long, Entity> groupMap : entities.values()) {
			Collection<Entity> groupCollection = groupMap.values();
			groupCollection.removeIf(e -> {
				if (!e.isAlive()) {
					listeners.forEach(l -> l.entityRemoved(e));
					return true;
				}
				return false;
			});
			groupCollection.forEach(Entity::update);
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

	public void writeUpdate(ByteBuffer byteBuffer) {
		byteBuffer.position(0);
		byteBuffer.put((byte) entities.size());
		for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
			byteBuffer.put(groupEntry.getKey().getId());
			Map<Long, Entity> entityMap = groupEntry.getValue();
			byteBuffer.putInt(entityMap.size());
			for (Entity e : entityMap.values()) {
				byteBuffer.putLong(e.getId());
				byteBuffer.put(EntityRegistry.getIdOf(e.getClass()));
				e.writeUpdate(byteBuffer);
			}
		}

	}

	public void applyUpdate(ByteBuffer byteBuffer) {
		byteBuffer.position(0);
		byte groupCount = byteBuffer.get();
		for (int i = 0; i < groupCount; i++) {
			EntityGroup group = EntityGroup.values()[byteBuffer.get()];
			Map<Long, Entity> groupMap = entities.get(group);
			groupMap.values().forEach(e -> e.setAlive(false));
			int groupSize = byteBuffer.getInt();
			for (int j = 0; j < groupSize; j++) {
				long entityId = byteBuffer.getLong();
				byte classId = byteBuffer.get();
				Entity e = groupMap.get(entityId);
				if (e == null) {
					e = EntityRegistry.newEntity(classId);
					e.setId(entityId);
					add(e);
				}
				e.setAlive(true);
				e.applyUpdate(byteBuffer);
			}
		}
	}
}
