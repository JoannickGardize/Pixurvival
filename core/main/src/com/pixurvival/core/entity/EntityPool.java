package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.pixurvival.core.World;

/**
 * This class contains all entities of a given {@link World}, packed by group
 * defined in enum {@link EntityGroup}.
 * 
 * @author SharkHendirx
 *
 */
public class EntityPool {
	private World world;
	private Map<EntityGroup, Map<Long, Entity>> entities = new EnumMap<>(EntityGroup.class);
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
		if (world.isServer()) {
			e.setWorld(world);
			e.setId(nextId++);
		}
		entities.get(e.getGroup()).put(e.getId(), e);
		e.initialize();
		listeners.forEach(l -> l.entityAdded(e));
	}

	public void update() {
		for (Map<Long, Entity> groupMap : entities.values()) {
			Collection<Entity> groupCollection = groupMap.values();
			groupCollection.removeIf(e -> {
				if (!e.isAlive()) {
					listeners.forEach(l -> l.entityRemoved(e));
					if (e.getChunk() != null) {
						e.getChunk().getEntities().remove(e);
					}
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

	public void applyUpdate(ByteBuffer byteBuffer) {
		byte groupId;
		while ((groupId = byteBuffer.get()) != EntityGroup.END_MARKER) {
			EntityGroup group = EntityGroup.values()[groupId];
			Map<Long, Entity> groupMap = entities.get(group);
			short size = byteBuffer.getShort();
			for (int i = 0; i < size; i++) {
				long entityId = byteBuffer.getLong();
				Entity e = groupMap.get(entityId);
				if (e == null) {
					e = group.getEntitySupplier().get();
					e.setId(entityId);
					e.setWorld(world);
					e.applyUpdate(byteBuffer);
					add(e);
				} else {
					e.applyUpdate(byteBuffer);
				}
			}
		}
		while ((groupId = byteBuffer.get()) != EntityGroup.END_MARKER) {
			EntityGroup group = EntityGroup.values()[groupId];
			Map<Long, Entity> groupMap = entities.get(group);
			short size = byteBuffer.getShort();
			for (int i = 0; i < size; i++) {
				long entityId = byteBuffer.getLong();
				Entity e = groupMap.get(entityId);
				if (e != null) {
					e.setAlive(false);
				}
			}
		}
	}

	public void writeEntityReference(ByteBuffer byteBuffer, Entity entity) {
		byteBuffer.put((byte) entity.getGroup().ordinal());
		byteBuffer.putLong(entity.getId());
	}

	public Entity readEntityReference(ByteBuffer byteBuffer) {
		EntityGroup group = EntityGroup.values()[byteBuffer.get()];
		return entities.get(group).get(byteBuffer.getLong());
	}
}
