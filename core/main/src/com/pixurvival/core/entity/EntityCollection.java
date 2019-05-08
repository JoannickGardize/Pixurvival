package com.pixurvival.core.entity;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class EntityCollection {

	private Map<EntityGroup, Map<Long, Entity>> entities = new EnumMap<>(EntityGroup.class);

	public Collection<Entity> get(EntityGroup group) {
		Map<Long, Entity> groupMap = entities.get(group);
		return groupMap == null ? Collections.emptyList() : groupMap.values();
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

	public void clear() {
		entities.values().forEach(Map::clear);
	}

	public void addAll(EntityCollection other) {
		other.entities.entrySet().forEach(entry -> entities.computeIfAbsent(entry.getKey(), key -> new HashMap<>()).putAll(entry.getValue()));
	}

	public void addAll(List<Entity> entityList) {
		entityList.forEach(this::add);
	}

	public void writeUpdate(ByteBuffer byteBuffer, boolean onlyChanged) {
		for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
			Map<Long, Entity> entityMap = groupEntry.getValue();
			if (entityMap.isEmpty()) {
				continue;
			}
			byteBuffer.put((byte) groupEntry.getKey().ordinal());
			int sizePosition = byteBuffer.position();
			byteBuffer.putShort((short) 0);
			short size = 0;
			for (Entity e : entityMap.values()) {
				if (!onlyChanged || e.isStateChanged()) {
					size++;
					byteBuffer.putLong(e.getId());
					e.writeUpdate(byteBuffer);
				}
			}
			if (size == 0) {
				byteBuffer.position(sizePosition - 1);
			} else {
				byteBuffer.putShort(sizePosition, size);
			}
		}
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

}
