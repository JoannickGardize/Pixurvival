package com.pixurvival.core.map;

import java.lang.ref.SoftReference;

import lombok.Getter;

public class Chunk {

	public static final int CHUNK_SIZE = 32;

	@Getter
	private MapTile[] tiles;

	@Getter
	private Position position;

	@Getter
	private int offsetX;
	@Getter
	private int offsetY;

	private SoftReference<CompressedChunk> compressedChunkRef = new SoftReference<>(null);

	public Chunk(int x, int y) {
		this.position = new Position(x, y);
		offsetX = position.getX() * CHUNK_SIZE;
		offsetY = position.getY() * CHUNK_SIZE;
		tiles = new MapTile[CHUNK_SIZE * CHUNK_SIZE];
	}

	public MapTile tileAtLocal(int x, int y) {
		try {
			return tiles[y * CHUNK_SIZE + x];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw e;
		}
	}

	public MapTile tileAt(int x, int y) {
		return tileAtLocal(x - offsetX, y - offsetY);
	}

	public void set(int x, int y, MapTile tile) {
		tiles[y * CHUNK_SIZE + x] = tile;
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
