package com.pixurvival.core.map;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Chunk {

	private static final long KEEP_ALIVE_MILLIS = 10_000;

	private TiledMap map;

	private MapTile[] tiles;

	private List<MapStructure> structures = new ArrayList<>();

	private ChunkPosition position;

	private int offsetX;

	private int offsetY;

	private SoftReference<CompressedChunk> compressedChunkRef = new SoftReference<>(null);

	private @Setter long updateTimestamp;

	private long lastCheckTimestamp;

	private @Setter boolean fileSync = false;

	public Chunk(TiledMap map, int x, int y) {
		this.map = map;
		this.position = new ChunkPosition(x, y);
		updateTimestamp = map.getWorld().getTime().getTimeMillis();
		offsetX = position.getX() * GameConstants.CHUNK_SIZE;
		offsetY = position.getY() * GameConstants.CHUNK_SIZE;
		tiles = new MapTile[GameConstants.CHUNK_SIZE * GameConstants.CHUNK_SIZE];
		check();
	}

	public void check() {
		lastCheckTimestamp = System.currentTimeMillis();
	}

	public boolean isTimeout() {
		return System.currentTimeMillis() - lastCheckTimestamp >= KEEP_ALIVE_MILLIS;
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

	public void set(int x, int y, MapTile tile) {
		tiles[y * GameConstants.CHUNK_SIZE + x] = tile;
		fileSync = false;
	}

	public MapStructure addStructure(Structure structure, int x, int y) {
		return addStructure(structure, x, y, true);
	}

	public MapStructure addStructure(Structure structure, int x, int y, boolean notify) {
		MapStructure mapStructure = MapStructure.newInstance(this, structure, x, y);
		int localX = x - offsetX;
		int localY = y - offsetY;
		for (int cx = localX; cx < localX + structure.getDimensions().getWidth(); cx++) {
			for (int cy = localY; cy < localY + structure.getDimensions().getHeight(); cy++) {
				Tile tile = tileAtLocal(cx, cy).getTileDefinition();
				TileAndStructure tileAndStructure = new TileAndStructure(tile, mapStructure);
				set(cx, cy, tileAndStructure);
			}
		}
		structures.add(mapStructure);
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
			int localX = structure.getTileX() - offsetX;
			int localY = structure.getTileY() - offsetY;
			for (int sx = localX; sx < localX + structure.getWidth(); sx++) {
				for (int sy = localY; sy < localY + structure.getHeight(); sy++) {
					set(sx, sy, map.getMapTilesById()[tile.getTileDefinition().getId()]);
				}
			}
			structures.remove(structure);
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
