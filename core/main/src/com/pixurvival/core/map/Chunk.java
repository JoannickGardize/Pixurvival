package com.pixurvival.core.map;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;

import lombok.Getter;

public class Chunk {

	@Getter
	private MapTile[] tiles;
	@Getter
	private List<MapStructure> structures = new ArrayList<>();

	@Getter
	private Position position;

	@Getter
	private int offsetX;
	@Getter
	private int offsetY;

	private SoftReference<CompressedChunk> compressedChunkRef = new SoftReference<>(null);

	public Chunk(int x, int y) {
		this.position = new Position(x, y);
		offsetX = position.getX() * GameConstants.CHUNK_SIZE;
		offsetY = position.getY() * GameConstants.CHUNK_SIZE;
		tiles = new MapTile[GameConstants.CHUNK_SIZE * GameConstants.CHUNK_SIZE];
	}

	public MapTile tileAtLocal(int x, int y) {
		try {
			return tiles[y * GameConstants.CHUNK_SIZE + x];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw e;
		}
	}

	public MapTile tileAt(int x, int y) {
		return tileAtLocal(x - offsetX, y - offsetY);
	}

	public void set(int x, int y, MapTile tile) {
		tiles[y * GameConstants.CHUNK_SIZE + x] = tile;
	}

	public void addStructure(Structure structure, int x, int y) {
		MapStructure mapStructure = MapStructure.fromStructure(structure, x, y);
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
