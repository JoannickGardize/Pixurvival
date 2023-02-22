package com.pixurvival.core.entity;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.chunk.ChunkGroupChangeHelper;
import com.pixurvival.core.util.LongSequenceIOHelper;
import lombok.AccessLevel;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A collection of {@link Entity} separated by group and accessible by their
 * IDs. Able to serialize / deserialize the entire collection for a full, delta,
 * or repository update.
 *
 * @author SharkHendrix
 */
public class EntityCollection {

    private interface GroupWriter {
        boolean writeUpdate(ByteBuffer byteBuffer, Map<Long, Entity> entityMap);
    }

    private @Getter(AccessLevel.PROTECTED) Map<EntityGroup, Map<Long, Entity>> entities = new EnumMap<>(EntityGroup.class);

    public Collection<Entity> get(EntityGroup group) {
        return entities.computeIfAbsent(group, key -> new HashMap<>()).values();
    }

    protected Map<Long, Entity> getMap(EntityGroup group) {
        return entities.computeIfAbsent(group, key -> new HashMap<>());
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

    public Map<Long, Entity> removeGroup(EntityGroup group) {
        return entities.remove(group);
    }

    public void setGroup(EntityGroup group, Map<Long, Entity> groupEntities) {
        entities.put(group, groupEntities);
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

    private void writeUpdate(ByteBuffer buffer, GroupWriter writer) {
        for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
            Map<Long, Entity> entityMap = groupEntry.getValue();
            if (entityMap.isEmpty()) {
                continue;
            }
            buffer.put((byte) groupEntry.getKey().ordinal());
            if (!writer.writeUpdate(buffer, entityMap)) {
                buffer.position(buffer.position() - 2);
            }
        }
    }

    private boolean writeDeltaUpdate(ByteBuffer buffer, Map<Long, Entity> entityMap, ChunkGroupChangeHelper chunkVision) {
        LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
        boolean anyWrite = false;
        for (Entity e : entityMap.values()) {
            if (!chunkVision.contains(e.getPreviousUpdateChunkPosition())) {
                anyWrite |= writeEntity(buffer, e, true, idSequence);
            } else if (e.isStateChanged()) {
                anyWrite |= writeEntity(buffer, e, false, idSequence);
            }
        }
        idSequence.reWriteLast(buffer);
        return anyWrite;
    }

    private boolean writeFullUpdate(ByteBuffer buffer, Map<Long, Entity> entityMap) {
        LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
        boolean anyWrite = false;
        for (Entity e : entityMap.values()) {
            anyWrite |= writeEntity(buffer, e, true, idSequence);
        }
        idSequence.reWriteLast(buffer);
        return anyWrite;
    }

    private boolean writeRepositoryUpdate(ByteBuffer buffer, Map<Long, Entity> entityMap) {
        LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
        for (Entity e : entityMap.values()) {
            idSequence.write(buffer, e.getId());
            e.writeInitialization(buffer);
            e.writeRepositoryUpdate(buffer);
        }
        idSequence.reWriteLast(buffer);
        return !entityMap.isEmpty();
    }

    /**
     * @param byteBuffer
     * @param e
     * @param full
     * @param idSequence
     * @return true if the entity has been written, false if the entity has been
     * ignored and nothing has been written
     */
    private boolean writeEntity(ByteBuffer byteBuffer, Entity e, boolean full, LongSequenceIOHelper idSequence) {
        if (e.isInvisible()) {
            return false;
        }
        idSequence.write(byteBuffer, e.getId());
        e.writeInitialization(byteBuffer);
        e.writeUpdate(byteBuffer, full);
        return true;
    }

    public void writeAllIds(ByteBuffer buffer) {
        for (Entry<EntityGroup, Map<Long, Entity>> groupEntry : entities.entrySet()) {
            Map<Long, Entity> entityMap = groupEntry.getValue();
            if (entityMap.isEmpty()) {
                continue;
            }
            buffer.put((byte) groupEntry.getKey().ordinal());
            LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
            for (Entity e : entityMap.values()) {
                idSequence.write(buffer, e.getId());
            }
            idSequence.reWriteLast(buffer);
        }
    }

    public void applyUpdate(ByteBuffer byteBuffer, World world) {
        applyUpdate(byteBuffer, world, false);
    }

    public void applyUpdate(ByteBuffer byteBuffer, World world, boolean repositoryMode) {
        byte groupId;
        // Entity removes
        // TODO stash useless ?
        Map<Long, Entity> stash = applyRemove(byteBuffer);
        // Entity updates
        while ((groupId = byteBuffer.get()) != EntityGroup.END_MARKER) {
            EntityGroup group = EntityGroup.values()[groupId];
            Map<Long, Entity> groupMap = entities.computeIfAbsent(group, key -> new HashMap<>());
            LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
            long previousId = 0;
            long currentId = 0;
            while ((currentId = idSequence.read(byteBuffer)) != previousId) {
                Entity e = groupMap.get(currentId);
                if (e == null) {
                    if ((e = stash.get(currentId)) == null) {
                        e = createEntity(group, world, currentId);
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
                previousId = currentId;
            }
        }
        // Far allies positions update
        applyFarAllies(byteBuffer, world);
    }

    private Map<Long, Entity> applyRemove(ByteBuffer buffer) {
        byte groupId;
        Map<Long, Entity> stash = Collections.emptyMap();
        while ((groupId = buffer.get()) != EntityGroup.END_MARKER) {
            if (groupId == EntityGroup.REMOVE_ALL_MARKER) {
                buffer.get();
                stash = new HashMap<>();
                entities.values().forEach(stash::putAll);
                clear();
                break;
            }
            EntityGroup group = EntityGroup.values()[groupId];
            Map<Long, Entity> groupMap = entities.computeIfAbsent(group, key -> new HashMap<>());
            LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
            long previousId = 0;
            long currentId = 0;
            while ((currentId = idSequence.read(buffer)) != previousId) {
                Entity e = groupMap.get(currentId);
                if (e != null) {
                    e.setAlive(false);
                }
                previousId = currentId;
            }
        }
        return stash;
    }

    private void applyFarAllies(ByteBuffer byteBuffer, World world) {
        Map<Long, PlayerEntity> groupMap = world.getPlayerEntities();
        LongSequenceIOHelper idSequence = new LongSequenceIOHelper();
        long previousId = 0;
        long currentId = 0;
        while ((currentId = idSequence.read(byteBuffer)) != previousId) {
            PlayerEntity e = groupMap.get(currentId);
            if (e != null) {
                e.getPosition().set(byteBuffer.getFloat(), byteBuffer.getFloat());
                e.getVelocity().set(byteBuffer.getFloat(), byteBuffer.getFloat());
            } else {
                byteBuffer.position(byteBuffer.position() + 16);
            }
            previousId = currentId;
        }
    }

    protected Entity createEntity(EntityGroup group, World world, long id) {
        Entity e = group.getEntitySupplier().get(world, id);
        e.setId(id);
        return e;
    }
}
