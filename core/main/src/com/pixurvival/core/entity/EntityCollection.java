package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.chunk.ChunkGroupChangeHelper;

import lombok.AccessLevel;
import lombok.Getter;

public class EntityCollection {

	private @Getter(AccessLevel.PROTECTED) Map<EntityGroup, Map<Long, Entity>> entities = new EnumMap<>(EntityGroup.class);

	public Collection<Entity> get(EntityGroup group) {
		Map<Long, Entity> groupMap = entities.get(group);
		return groupMap == null ? Collections.emptyList() : groupMap.values();
	}

	protected Map<Long, Entity> getMap(EntityGroup group) {
		Map<Long, Entity> groupMap = entities.get(group);
		return groupMap == null ? Collections.emptyMap() : groupMap;
	}

	public void add(Entity entity) {
		Map<Long, Entity> groupMap = entities.computeIfAbsent(entity.getGroup(), key -> new HashMap<>());
		groupMap.put(entity.getId(), entity);
	}

	public void remove(Entity entity) {
		Map<Long, Entity> groupMap = entities.get(entity.getGroup());
		if (groupMap != null) {
			groupMap.remove(entity.getId());
		}
	}

	public void foreach(Consumer<Entity> action) {
		entities.values().forEach(map -> map.values().forEach(action));
	}

	public void foreach(BiConsumer<? super EntityGroup, ? super Map<Long, Entity>> action) {
		entities.forEach(action);
	}

	public Entity get(EntityGroup group, long id) {
		return getMap(group).get(id);
	}

	public void clear() {
		entities.values().forEach(Map::clear);
	}

	public void addAll(EntityCollection other) {
		other.entities.entrySet().forEach(entry -> entities.computeIfAbsent(entry.getKey(), key -> new HashMap<>()).putAll(entry.getValue()));
	}

	public void addAll(List<Entity> entityList) {
		entityList.forEach(this::add);
	}

	public void writeUpdate(ByteBuffer byteBuffer, boolean full, ChunkGroupChangeHelper chunkVision) {
		for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
			Map<Long, Entity> entityMap = groupEntry.getValue();
			if (entityMap.isEmpty()) {
				continue;
			}
			byteBuffer.put((byte) groupEntry.getKey().ordinal());
			int sizePosition = byteBuffer.position();
			byteBuffer.putShort((short) 0);
			short size = 0;
			if (full) {
				for (Entity e : entityMap.values()) {
					writeEntity(byteBuffer, e, true);
				}
				size += entityMap.size();
			} else {
				for (Entity e : entityMap.values()) {
					if (!chunkVision.contains(e.getPreviousUpdateChunkPosition())) {
						size++;
						writeEntity(byteBuffer, e, true);
					} else if (e.isStateChanged()) {
						size++;
						writeEntity(byteBuffer, e, false);
					}
				}
			}
			if (size == 0) {
				byteBuffer.position(sizePosition - 1);
			} else {
				byteBuffer.putShort(sizePosition, size);
			}
		}
	}

	private void writeEntity(ByteBuffer byteBuffer, Entity e, boolean full) {
		byteBuffer.putLong(e.getId());
		e.writeInitialization(byteBuffer);
		e.writeUpdate(byteBuffer, full);
	}

	public void writeAllIds(ByteBuffer byteBuffer) {
		for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
			Map<Long, Entity> entityMap = groupEntry.getValue();
			if (entityMap.isEmpty()) {
				continue;
			}
			byteBuffer.put((byte) groupEntry.getKey().ordinal());
			byteBuffer.putShort((short) entityMap.size());
			for (Entity e : entityMap.values()) {
				byteBuffer.putLong(e.getId());
			}
		}
	}

	public void applyUpdate(ByteBuffer byteBuffer, World world) {
		byte groupId;
		// Entity updates
		while ((groupId = byteBuffer.get()) != EntityGroup.END_MARKER) {
			EntityGroup group = EntityGroup.values()[groupId];
			Map<Long, Entity> groupMap = entities.computeIfAbsent(group, key -> new HashMap<>());
			short size = byteBuffer.getShort();
			for (int i = 0; i < size; i++) {
				long entityId = byteBuffer.getLong();
				Entity e = groupMap.get(entityId);
				if (e == null) {
					e = createEntity(group, world, entityId);
					e.setWorld(world);
					e.applyInitialization(byteBuffer);
					add(e);
				} else {
					// Du fait de l'UDP, les données d'initialisation sont quand
					// même envoyés pour éponger les problèmes de paquets
					// perdus.
					e.applyInitialization(byteBuffer);
				}
				e.applyUpdate(byteBuffer);
			}
		}
		// Entity removes
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
		// Far allies positions update
		short length = byteBuffer.getShort();
		Map<Long, PlayerEntity> groupMap = world.getPlayerEntities();
		for (int i = 0; i < length; i++) {
			long id = byteBuffer.getLong();
			PlayerEntity e = groupMap.get(id);
			if (e != null) {
				e.getPosition().set(byteBuffer.getDouble(), byteBuffer.getDouble());
				e.getVelocity().set(byteBuffer.getDouble(), byteBuffer.getDouble());
			} else {
				byteBuffer.getDouble();
				byteBuffer.getDouble();
				byteBuffer.getDouble();
				byteBuffer.getDouble();
			}
		}
	}

	protected Entity createEntity(EntityGroup group, World world, long id) {
		Entity e = group.getEntitySupplier().get(world, id);
		e.setId(id);
		return e;
	}
}
