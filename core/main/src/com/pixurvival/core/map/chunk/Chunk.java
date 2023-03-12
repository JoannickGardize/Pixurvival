package com.pixurvival.core.map.chunk;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.entity.EntityCollection;
import com.pixurvival.core.map.*;
import com.pixurvival.core.map.chunk.update.StructureUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

@Getter
public class Chunk {

    private static final long KEEP_ALIVE_MILLIS = 10_000;

    private static final SoftReference<CompressedChunk> NULL_COMPRESSED_REF = new SoftReference<>(null);

    private TiledMap map;

    private MapTile[] tiles;

    // TODO IN PROGRESS
    private Tile[] overflowingTiles;

    private @Getter(AccessLevel.NONE) Map<Integer, List<StructureEntity>> structures = new HashMap<>();

    private ChunkPosition position;

    private int offsetX;

    private int offsetY;

    private @Getter(AccessLevel.NONE) SoftReference<CompressedChunk> compressedChunkRef = NULL_COMPRESSED_REF;

    private @Setter long updateTimestamp;

    private long lastCheckTimestamp;

    private @Setter boolean fileSync = false;

    private EntityCollection entities = new EntityCollection();

    private Map<Object, Light> lights = new HashMap<>();

    private @Setter boolean newlyCreated = false;

    private int structureCount = 0;

    private @Setter int factoryCount = 0;

    public Chunk(TiledMap map, int x, int y) {
        this.map = map;
        this.position = new ChunkPosition(x, y);
        updateTimestamp = map.getWorld().getTime().getTimeMillis();
        offsetX = position.getX() * GameConstants.CHUNK_SIZE;
        offsetY = position.getY() * GameConstants.CHUNK_SIZE;
        tiles = new MapTile[GameConstants.CHUNK_SIZE * GameConstants.CHUNK_SIZE];
        check();
    }

    public List<StructureEntity> getStructures(int typeId) {
        List<StructureEntity> list = structures.get(typeId);
        if (list == null) {
            return Collections.emptyList();
        } else {
            return list;
        }
    }

    public Collection<Light> getLights() {
        return lights.values();
    }

    public Set<Entry<Integer, List<StructureEntity>>> getStructures() {
        return structures.entrySet();
    }

    public void check() {
        lastCheckTimestamp = map.getWorld().getTime().getTimeMillis();
    }

    public boolean isTimeout() {
        return map.getWorld().getTime().getTimeMillis() - lastCheckTimestamp >= KEEP_ALIVE_MILLIS;
    }

    public void updateTimestamp() {
        updateTimestamp = map.getWorld().getTime().getTimeMillis();
        invalidateCompressed();
    }

    public void invalidateCompressed() {
        // The CompressedChunk is not up to date anymore
        compressedChunkRef = NULL_COMPRESSED_REF;
    }

    public MapTile tileAtLocal(int x, int y) {
        return tiles[y * GameConstants.CHUNK_SIZE + x];
    }

    public MapTile tileAt(int x, int y) {
        return tileAtLocal(x - offsetX, y - offsetY);
    }

    public boolean containsTile(int x, int y) {
        int localX = x - offsetX;
        int localY = y - offsetY;
        return localX >= 0 && localX < GameConstants.CHUNK_SIZE && localY >= 0 && localY < GameConstants.CHUNK_SIZE;
    }

    public void set(int x, int y, MapTile tile) {
        tiles[y * GameConstants.CHUNK_SIZE + x] = tile;
        fileSync = false;
    }

    public void forEachStructure(Consumer<StructureEntity> action) {
        structures.values().forEach(list -> list.forEach(action));
    }

    public StructureEntity addNewStructure(Structure structure, int x, int y) {
        StructureEntity mapStructure = createStructure(structure, x, y);
        mapStructure.initiliazeNewlyCreated();
        notifyStructureAdded(mapStructure);
        return mapStructure;
    }

    public StructureEntity addStructure(Structure structure, int x, int y, long id) {
        StructureEntity mapStructure = createStructure(structure, x, y);
        mapStructure.setId(id);
        notifyStructureAdded(mapStructure);
        return mapStructure;
    }

    /**
     * Does not initialize id
     *
     * @param structure
     * @param x
     * @param y
     * @return
     */
    public StructureEntity addStructureSilently(Structure structure, int x, int y) {
        StructureEntity mapStructure = createStructure(structure, x, y);
        return mapStructure;
    }

    private StructureEntity createStructure(Structure structure, int x, int y) {
        // TODO ajouter les structure sur tous les chunks plutôt qu'un seul
        StructureEntity mapStructure = structure.newStructureEntity(this, x, y);
        if (structure.getLightEmissionRadius() > 0) {
            lights.put(mapStructure, new Light(mapStructure.getPosition(), structure.getLightEmissionRadius()));
        }
        int localX = x - offsetX;
        int localY = y - offsetY;
        for (int cx = localX; cx < localX + structure.getDimensions().getWidth(); cx++) {
            for (int cy = localY; cy < localY + structure.getDimensions().getHeight(); cy++) {
                Tile tile = tileAtLocal(cx, cy).getTileDefinition();
                TileAndStructure tileAndStructure = new TileAndStructure(tile, mapStructure);
                set(cx, cy, tileAndStructure);
            }
        }
        structures.computeIfAbsent(structure.getId(), id -> new ArrayList<>()).add(mapStructure);
        structureCount++;
        return mapStructure;
    }

    private void notifyStructureAdded(StructureEntity mapStructure) {
        updateTimestamp();
        getMap().notifyStructureAdded(mapStructure);
        fileSync = false;
    }

    public boolean isFree(int x, int y, int width, int height) {
        int localX = x - offsetX;
        int localY = y - offsetY;
        return isEmptyLocal(localX, localY, width, height);
    }

    public boolean isEmptyLocal(int x, int y, int width, int height) {

        for (int cx = x; cx < x + width; cx++) {
            for (int cy = y; cy < y + height; cy++) {
                if (tileAtLocal(cx, cy).getStructure() != null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void removeStructure(int x, int y) {
        removeStructure(x, y, true);
    }

    /**
     * Remove the structure at the given tile, in world coordinates. If there is no
     * structure at this tile, nothing happens.
     *
     * @param x
     * @param y
     * @param notify
     */
    public void removeStructure(int x, int y, boolean notify) {
        MapTile tile = tileAt(x, y);
        if (tile.getStructure() != null) {
            StructureEntity structure = tile.getStructure();
            if (structure.getDefinition().getLightEmissionRadius() > 0) {
                lights.remove(structure);
            }
            int localX = structure.getTileX() - offsetX;
            int localY = structure.getTileY() - offsetY;
            for (int sx = localX; sx < localX + structure.getWidth(); sx++) {
                for (int sy = localY; sy < localY + structure.getHeight(); sy++) {
                    set(sx, sy, map.getMapTilesById()[tile.getTileDefinition().getId()]);
                }
            }
            structures.get(structure.getDefinition().getId()).remove(structure);
            structureCount--;
            updateTimestamp();
            if (notify) {
                getMap().notifyStructureRemoved(structure);

            }
            fileSync = false;
        }
    }

    public void notifyStructureChanged(StructureEntity mapStructure, StructureUpdate structureUpdate) {
        updateTimestamp();
        map.notifyStructureChanged(mapStructure, structureUpdate);
    }

    public CompressedChunk getCompressed() {
        CompressedChunk compressed = compressedChunkRef.get();
        if (compressed == null) {
            compressed = new CompressedChunk(this);
            compressedChunkRef = new SoftReference<>(compressed);
        }
        return compressed;
    }

    public void setCompressed(CompressedChunk compressed) {
        compressedChunkRef = new SoftReference<>(compressed);
    }

    public Random createFixedRandom() {
        return new Random((long) position.getX() << 32L ^ position.getY());
    }

    @Override
    public String toString() {
        return "Chunk at " + position;
    }
}
