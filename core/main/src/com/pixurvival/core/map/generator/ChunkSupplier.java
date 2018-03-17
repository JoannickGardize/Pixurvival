package com.pixurvival.core.map.generator;

import java.util.List;
import java.util.Random;

import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Structure;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.EmptyTile;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.MapTile;

import lombok.Getter;

public class ChunkSupplier {

	private MapGenerator mapGenerator;
	private long seed;

	@Getter
	private MapTile[] mapTilesById;

	public ChunkSupplier(List<Tile> tilesById, MapGenerator mapGenerator, long seed) {
		mapGenerator.initialize(seed);
		this.mapGenerator = mapGenerator;
		this.seed = seed;
		mapTilesById = new MapTile[tilesById.size()];
		for (int i = 0; i < tilesById.size(); i++) {
			mapTilesById[i] = new EmptyTile(tilesById.get(i));
		}
	}

	public Chunk get(int x, int y) {
		Chunk chunk = new Chunk(x, y);
		buildTiles(chunk);
		buildStructures(chunk);
		return chunk;
	}

	private void buildTiles(Chunk chunk) {
		for (int cx = 0; cx < Chunk.CHUNK_SIZE; cx++) {
			for (int cy = 0; cy < Chunk.CHUNK_SIZE; cy++) {
				chunk.set(cx, cy,
						mapTilesById[mapGenerator.getTileAt(chunk.getPosition().getX() * Chunk.CHUNK_SIZE + cx,
								chunk.getPosition().getY() * Chunk.CHUNK_SIZE + cy).getId()]);
			}
		}
	}

	private void buildStructures(Chunk chunk) {
		int x = chunk.getPosition().getX();
		int y = chunk.getPosition().getY();
		Random chunkRandom = new Random((seed << 32) ^ (x << 16) ^ y);
		for (int cx = 0; cx < Chunk.CHUNK_SIZE; cx++) {
			for (int cy = 0; cy < Chunk.CHUNK_SIZE; cy++) {
				if (chunk.tileAtLocal(cx, cy) instanceof EmptyTile) {
					Structure structure = mapGenerator.getStructureAt(x * Chunk.CHUNK_SIZE + cx,
							y * Chunk.CHUNK_SIZE + cy, chunkRandom);
					if (structure != null && cx <= Chunk.CHUNK_SIZE - structure.getDimensions().getWidth()
							&& cy <= Chunk.CHUNK_SIZE - structure.getDimensions().getHeight()
							&& !hasStructure(chunk, cx, cy, structure.getDimensions().getWidth(),
									structure.getDimensions().getHeight())) {
						chunk.addStructure(structure, x * Chunk.CHUNK_SIZE + cx, y * Chunk.CHUNK_SIZE + cy);
					}
				}
			}
		}
	}

	private boolean hasStructure(Chunk chunk, int x, int y, int width, int height) {
		for (int cx = x; cx < x + width; cx++) {
			for (int cy = y; cy < y + height; cy++) {
				if (chunk.tileAtLocal(cx, cy) instanceof MapStructure) {
					return true;
				}
			}
		}
		return false;
	}

}
