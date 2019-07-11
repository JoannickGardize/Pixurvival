package com.pixurvival.core.map.generator;

import java.util.Random;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.EmptyTile;
import com.pixurvival.core.map.MapTile;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.chunk.Chunk;

public class ChunkSupplier {

	private TiledMap map;
	private MapGenerator mapGenerator;
	private long seed;

	public ChunkSupplier(World world, MapGenerator mapGenerator, long seed) {
		map = world.getMap();

		mapGenerator.initialize(seed);
		this.mapGenerator = mapGenerator;
		this.seed = seed;

	}

	public Chunk get(int x, int y) {
		Chunk chunk = new Chunk(map, x, y);
		buildTiles(chunk);
		buildStructures(chunk);
		return chunk;
	}

	private void buildTiles(Chunk chunk) {
		for (int cx = 0; cx < GameConstants.CHUNK_SIZE; cx++) {
			for (int cy = 0; cy < GameConstants.CHUNK_SIZE; cy++) {
				chunk.set(cx, cy,
						map.getMapTilesById()[mapGenerator.getTileAt(chunk.getPosition().getX() * GameConstants.CHUNK_SIZE + cx, chunk.getPosition().getY() * GameConstants.CHUNK_SIZE + cy).getId()]);
			}
		}
	}

	private void buildStructures(Chunk chunk) {
		int x = chunk.getPosition().getX();
		int y = chunk.getPosition().getY();
		Random chunkRandom = new Random(seed << 32 ^ x << 16 ^ y);
		for (int cx = 0; cx < GameConstants.CHUNK_SIZE; cx++) {
			for (int cy = 0; cy < GameConstants.CHUNK_SIZE; cy++) {
				MapTile mapTile = chunk.tileAtLocal(cx, cy);
				if (mapTile instanceof EmptyTile) {
					Structure structure = mapGenerator.getStructureAt(x * GameConstants.CHUNK_SIZE + cx, y * GameConstants.CHUNK_SIZE + cy, mapTile.getTileDefinition(), chunkRandom);
					if (structure != null && cx <= GameConstants.CHUNK_SIZE - structure.getDimensions().getWidth() && cy <= GameConstants.CHUNK_SIZE - structure.getDimensions().getHeight()
							&& chunk.isEmptyLocal(cx, cy, structure.getDimensions().getWidth(), structure.getDimensions().getHeight())) {
						chunk.addStructure(structure, x * GameConstants.CHUNK_SIZE + cx, y * GameConstants.CHUNK_SIZE + cy, false);
					}
				}
			}
		}
	}

}
