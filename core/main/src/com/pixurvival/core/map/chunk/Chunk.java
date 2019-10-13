package com.pixurvival.core.map.chunk;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.entity.EntityCollection;
import com.pixurvival.core.map.Light;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.TileAndStructure;
import com.pixurvival.core.map.TiledMap;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Chunk {

	private static final long KEEP_ALIVE_MILLIS = 10_000;

	private TiledMap map;

	private MapTile[] tiles;

	private @Getter(AccessLevel.NONE) Map<Integer, List<MapStructure>> structures = new HashMap<>();

	private ChunkPosition position;

	private int offsetX;

	private int offsetY;

	private @Getter(AccessLevel.NONE) SoftReference<CompressedChunk> compressedChunkRef = new SoftReference<>(null);

	private @Setter long updateTimestamp;

	private @Setter long lastCheckTimestamp;

	private @Setter boolean fileSync = false;

	private EntityCollection entities = new EntityCollection();

	private Map<Object, Light> lights = new HashMap<>();

	private @Setter boolean newlyCreated = false;

	private short structureCount = 0;

	public Chunk(TiledMap map, int x, int y) {
		this.map = map;
		this.position = new ChunkPosition(x, y);
		updateTimestamp = map.getWorld().getTime().getTimeMillis();
		offsetX = position.getX() * GameConstants.CHUNK_SIZE;
		offsetY = position.getY() * GameConstants.CHUNK_SIZE;
		tiles = new MapTile[GameConstants.CHUNK_SIZE * GameConstants.CHUNK_SIZE];
		check();
	}

	public List<MapStructure> getStructures(int typeId) {
		List<MapStructure> list = structures.get(typeId);
		if (list == null) {
			return Collections.emptyList();
		} else {
			return list;
		}
	}

	public Collection<Light> getLights() {
		return lights.values();
	}

	public Set<Entry<Integer, List<MapStructure>>> getStructures() {
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

	public void forEachStructure(Consumer<MapStructure> action) {
		structures.values().forEach(list -> list.forEach(action));
	}

	public MapStructure addStructure(Structure structure, int x, int y) {
		return addStructure(structure, x, y, true);
	}

	public MapStructure addStructure(Structure structure, int x, int y, boolean notify) {
		// TODO ajouter les structure sur tout les chunks plutôt qu'un seul
		MapStructure mapStructure = structure.newMapStructure(this, x, y);
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
		if (notify) {
			getMap().notifyListeners(l -> l.structureAdded(mapStructure));
			fileSync = false;
		}
		return mapStructure;
	}

	public boolean isEmpty(int x, int y, int width, int height) {
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
		MapTile tile = tileAt(x, y);
		if (tile.getStructure() != null) {
			MapStructure structure = tile.getStructure();
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
			getMap().notifyListeners(l -> l.structureRemoved(structure));
			fileSync = false;
		}
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
}