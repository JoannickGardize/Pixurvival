package com.pixurvival.core.entity;

import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.PlayerEntity;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class contains all entities of a given {@link World}, packed by group
 * defined in enum {@link EntityGroup}.
 *
 * @author SharkHendirx
 */
public class EntityPool extends EntityCollection {
    private World world;
    // Starts to 1 because 0 is interpreted as the end of the list for
    // serialization
    private @Getter
    @Setter long nextId = 1;
    private List<EntityPoolListener> listeners = new ArrayList<>();
    private List<Entity> newEntities = new ArrayList<>();

    public EntityPool(World world) {
        this.world = world;
    }

    public void addListener(EntityPoolListener l) {
        listeners.add(l);
    }

    @Override
    public void add(Entity e) {
        newEntities.add(e);
        if (world.isServer()) {
            e.setWorld(world);
            e.updateChunk();
        }
        e.initialize();
    }

    /**
     * Add a recycled instance of entity, this is assumed that the entity is already
     * binded to the right world.
     *
     * @param e
     */
    public void addOld(Entity e) {
        newEntities.add(e);
    }

    /**
     * Add and initialize the creation of the given entity
     *
     * @param e
     */
    public void addNew(Entity e) {
        e.setId(nextId++);
        add(e);
        e.initializeAtCreation();
    }

    public void removeAll(EntityCollection collection) {
        collection.foreach((group, map) -> {
            Map<Long, Entity> groupMap = getMap(group);
            for (Entry<Long, Entity> entry : map.entrySet()) {
                groupMap.remove(entry.getKey());
                notifyRemoved(entry.getValue());
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
                    if (entity.getChunk() != null) {
                        entity.getChunk().getEntities().remove(entity);
                    }
                    it.remove();
                    entity.onDeath();
                    notifyRemoved(entity);
                }
            }
        }
        flushNewEntities();
    }

    private void notifyRemoved(Entity entity) {
        if (!entity.isSneakyDeath() && !entity.isInvisible()) {
            listeners.forEach(l -> l.entityRemoved(entity));
        } else {
            listeners.forEach(l -> l.sneakyEntityRemoved(entity));
        }
    }

    public void applyUpdate(ByteBuffer byteBuffer) {
        applyUpdate(byteBuffer, world);
    }

    @Override
    public void applyUpdate(ByteBuffer byteBuffer, World world) {
        super.applyUpdate(byteBuffer, world);
        flushNewEntities();
    }

    /**
     * Force adding the pending new entities without a call to update().
     */
    public void flushNewEntities() {
        for (Entity e : newEntities) {
            super.add(e);
            listeners.forEach(l -> l.entityAdded(e));
        }
        newEntities.clear();
    }

    @Override
    public void clear() {
        getEntities().values().forEach(map -> map.values().forEach(entity -> {
            if (entity.getChunk() != null) {
                entity.getChunk().getEntities().remove(entity);
                // set the chunk to null for the case of the instance is reused
                // by stash
                entity.setChunk(null);
            }
        }));
        super.clear();
    }

    public void notifyPlayerDied(PlayerEntity playerEntity) {
        listeners.forEach(l -> l.playerDied(playerEntity));
    }

    public void notifyPlayerRespawned(PlayerEntity playerEntity) {
        listeners.forEach(l -> l.playerRespawned(playerEntity));
    }

    @Override
    protected Entity createEntity(EntityGroup group, World world, long id) {
        Entity e = super.createEntity(group, world, id);
        if (e.getChunk() != null) {
            e.getChunk().getEntities().remove(e);
            e.setChunk(null);
        }
        e.setAlive(true);
        return e;
    }

}
