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
import com.pixurvival.core.util.LongSequenceIOHelper;

import lombok.AccessLevel;
import lombok.Getter;

public class EntityCollection {

	private interface GroupWriter {
		short writeUpdate(ByteBuffer byteBuffer, Map<Long, Entity> entityMap);
	}

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

	public void writeFullUpdate(ByteBuffer byteBuffer) {
		writeUpdate(byteBuffer, this::writeFullUpdate);
	}

	public void writeDeltaUpdate(ByteBuffer byteBuffer, ChunkGroupChangeHelper chunkVision) {
		writeUpdate(byteBuffer, (b, m) -> writeDeltaUpdate(b, m, chunkVision));
	}

	public void writeRepositoryUpdate(ByteBuffer byteBuffer) {
		writeUpdate(byteBuffer, this::writeRepositoryUpdate);
	}

	private void writeUpdate(ByteBuffer byteBuffer, GroupWriter writer) {
		for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
			Map<Long, Entity> entityMap = groupEntry.getValue();
			if (entityMap.isEmpty()) {
				continue;
			}
			byteBuffer.put((byte) groupEntry.getKey().ordinal());
			int sizePosition = byteBuffer.position();
			byteBuffer.putShort((short) 0);
			short size = writer.writeUpdate(byteBuffer, entityMap);
			if (size == 0) {
				byteBuffer.position(sizePosition - 1);
			} else {
				byteBuffer.putShort(sizePosition, size);
			}
		}
	}

	private short writeDeltaUpdate(ByteBuffer byteBuffer, Map<Long, Entity> entityMap, ChunkGroupChangeHelper chunkVision) {
		short size = 0;
		LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
		for (Entity e : entityMap.values()) {
			if (!chunkVision.contains(e.getPreviousUpdateChunkPosition())) {
				size += writeEntity(byteBuffer, e, true, idSequence);
			} else if (e.isStateChanged()) {
				size += writeEntity(byteBuffer, e, false, idSequence);
			}
		}
		return size;
	}

	private short writeFullUpdate(ByteBuffer byteBuffer, Map<Long, Entity> entityMap) {
		short size = 0;
		LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
		for (Entity e : entityMap.values()) {
			size += writeEntity(byteBuffer, e, true, idSequence);
		}
		return size;
	}

	private short writeRepositoryUpdate(ByteBuffer byteBuffer, Map<Long, Entity> entityMap) {
		LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
		for (Entity e : entityMap.values()) {
			idSequence.write(byteBuffer, e.getId());
			e.writeInitialization(byteBuffer);
			e.writeRepositoryUpdate(byteBuffer);
		}
		return (short) entityMap.size();
	}

	private int writeEntity(ByteBuffer byteBuffer, Entity e, boolean full, LongSequenceIOHelper idSequence) {
		if (e.isInvisible()) {
			return 0;
		}
		idSequence.write(byteBuffer, e.getId());
		e.writeInitialization(byteBuffer);
		e.writeUpdate(byteBuffer, full);
		return 1;
	}

	public void writeAllIds(ByteBuffer byteBuffer) {
		for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
			Map<Long, Entity> entityMap = groupEntry.getValue();
			if (entityMap.isEmpty()) {
				continue;
			}
			byteBuffer.put((byte) groupEntry.getKey().ordinal());
			byteBuffer.putShort((short) entityMap.size());
			LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
			for (Entity e : entityMap.values()) {
				idSequence.write(byteBuffer, e.getId());
			}
		}
	}

	public void applyUpdate(ByteBuffer byteBuffer, World world) {
		applyUpdate(byteBuffer, world, false);
	}

	public void applyUpdate(ByteBuffer byteBuffer, World world, boolean repositoryMode) {
		byte groupId;
		// Entity removes
		Map<Long, Entity> stash = applyRemove(byteBuffer);
		// Entity updates
		while ((groupId = byteBuffer.get()) != EntityGroup.END_MARKER) {
			EntityGroup group = EntityGroup.values()[groupId];
			Map<Long, Entity> groupMap = entities.computeIfAbsent(group, key -> new HashMap<>());
			short size = byteBuffer.getShort();
			LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
			for (int i = 0; i < size; i++) {

				long entityId = idSequence.read(byteBuffer);
				Entity e = groupMap.get(entityId);
				if (e == null) {
					if ((e = stash.get(entityId)) == null) {
						e = createEntity(group, world, entityId);
						e.setWorld(world);
					}
					e.applyInitialization(byteBuffer);
					add(e);
				} else {
					// Du fait de l'UDP, les données d'initialisation sont quand
					// même envoyés pour éponger les problèmes de paquets
					// perdus.
					e.applyInitialization(byteBuffer);
				}
				if (repositoryMode) {
					e.applyRepositoryUpdate(byteBuffer);
				} else {
					e.applyUpdate(byteBuffer);
				}
			}
		}
		// Far allies positions update
		applyFarAllies(byteBuffer, world);
	}

	private Map<Long, Entity> applyRemove(ByteBuffer byteBuffer) {
		byte groupId;
		Map<Long, Entity> stash = Collections.emptyMap();
		while ((groupId = byteBuffer.get()) != EntityGroup.END_MARKER) {
			if (groupId == EntityGroup.REMOVE_ALL_MARKER) {
				byteBuffer.get();
				stash = new HashMap<>();
				entities.values().forEach(stash::putAll);
				clear();
				break;
			}
			EntityGroup group = EntityGroup.values()[groupId];
			Map<Long, Entity> groupMap = entities.computeIfAbsent(group, key -> new HashMap<>());
			short size = byteBuffer.getShort();
			LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
			for (int i = 0; i < size; i++) {
				long entityId = idSequence.read(byteBuffer);
				Entity e = groupMap.get(entityId);
				if (e != null) {
					e.setAlive(false);
				}
			}
		}
		return stash;
	}

	private void applyFarAllies(ByteBuffer byteBuffer, World world) {
		short length = byteBuffer.getShort();
		Map<Long, PlayerEntity> groupMap = world.getPlayerEntities();
		LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
		for (int i = 0; i < length; i++) {
			long id = idSequence.read(byteBuffer);
			PlayerEntity e = groupMap.get(id);
			if (e != null) {
				e.getPosition().set(byteBuffer.getFloat(), byteBuffer.getFloat());
				e.getVelocity().set(byteBuffer.getFloat(), byteBuffer.getFloat());
			} else {
				byteBuffer.position(byteBuffer.position() + 16);
			}
		}
	}

	protected Entity createEntity(EntityGroup group, World world, long id) {
		Entity e = group.getEntitySupplier().get(world, id);
		e.setId(id);
		return e;
	}
}
