package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pixurvival.core.World;

/**
 * This class contains all entities of a given {@link World}, packed by group
 * defined in enum {@link EntityGroup}.
 * 
 * @author SharkHendirx
 *
 */
public class EntityPool extends EntityCollection {
	private World world;
	private long nextId = 0;
	private List<EntityPoolListener> listeners = new ArrayList<>();

	public EntityPool(World world) {
		this.world = world;
	}

	public void addListener(EntityPoolListener l) {
		listeners.add(l);
	}

	@Override
	public void add(Entity e) {
		if (world.isServer()) {
			e.setWorld(world);
			e.setId(nextId++);
		}
		super.add(e);
		e.initialize();
		listeners.forEach(l -> l.entityAdded(e));
	}

	/**
	 * For future entity persistence
	 * 
	 * @param collection
	 */
	@Deprecated
	public void sneakyAddAll(EntityCollection collection) {
		collection.foreach((group, map) -> {
			Map<Long, Entity> groupMap = getMap(group);
			for (Entry<Long, Entity> entry : map.entrySet()) {
				groupMap.put(entry.getKey(), entry.getValue());
				entry.getValue().initialize();
			}
		});
	}

	public void removeAll(EntityCollection collection) {
		collection.foreach((group, map) -> {
			Map<Long, Entity> groupMap = getMap(group);
			for (Entry<Long, Entity> entry : map.entrySet()) {
				groupMap.remove(entry.getKey());
				listeners.forEach(l -> l.entityRemoved(entry.getValue()));
			}
		});
	}

	public void update() {
		for (Map<Long, Entity> groupMap : getEntities().values()) {
			Collection<Entity> groupCollection = groupMap.values();
			Iterator<Entity> it = groupCollection.iterator();
			while (it.hasNext()) {
				Entity entity = it.next();
				if (entity.isAlive()) {
					entity.update();
				} else {
					entity.onDeath();
					if (entity.getChunk() != null) {
						entity.getChunk().getEntities().remove(entity);
					}
					it.remove();
					listeners.forEach(l -> l.entityRemoved(entity));
				}

			}
		}
	}

	public void applyUpdate(ByteBuffer byteBuffer) {
		applyUpdate(byteBuffer, world);
	}

	public void writeEntityReference(ByteBuffer byteBuffer, Entity entity) {
		byteBuffer.put((byte) entity.getGroup().ordinal());
		byteBuffer.putLong(entity.getId());
	}

	public Entity readEntityReference(ByteBuffer byteBuffer) {
		EntityGroup group = EntityGroup.values()[byteBuffer.get()];
		return getEntities().get(group).get(byteBuffer.getLong());
	}

}
