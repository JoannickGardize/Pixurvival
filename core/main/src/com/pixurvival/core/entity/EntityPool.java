package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;

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
	private List<Entity> tmpEntityList = new ArrayList<>();

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

	public Entity closest(EntityGroup group, Entity entity) {
		Entity closestEntity = null;
		double closestDistance = Double.POSITIVE_INFINITY;
		for (Entity e : entities.get(group).values()) {
			double distance = entity.distanceSquared(e);
			if (distance < closestDistance) {
				closestDistance = distance;
				closestEntity = e;
			}
		}
		return closestEntity;
	}

	public void foreach(Consumer<Entity> action) {
		entities.values().forEach(m -> m.values().forEach(action));
	}

	public void writeUpdate(PlayerEntity player, ByteBuffer byteBuffer) {
		byteBuffer.position(0);
		byteBuffer.put((byte) entities.size());
		for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
			byteBuffer.put(groupEntry.getKey().getId());
			Map<Long, Entity> entityMap = groupEntry.getValue();
			tmpEntityList.clear();
			for (Entity e : entityMap.values()) {
				if (player.distanceSquared(e) <= GameConstants.PLAYER_ENTITY_VIEW_DISTANCE * GameConstants.PLAYER_ENTITY_VIEW_DISTANCE) {
					tmpEntityList.add(e);
				}
			}
			byteBuffer.putInt(tmpEntityList.size());
			for (Entity e : tmpEntityList) {
				byteBuffer.putLong(e.getId());
				byteBuffer.put(EntityClassRegistry.getIdOf(e.getClass()));
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
					e = EntityClassRegistry.newEntity(classId);
					e.setId(entityId);
					e.setWorld(world);
					e.applyUpdate(byteBuffer);
					add(e);
				} else {
					e.applyUpdate(byteBuffer);
				}
				e.setAlive(true);
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
