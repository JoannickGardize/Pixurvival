package com.pixurvival.core.map.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.EmptyTile;
import com.pixurvival.core.map.MapTile;

public class ChunkSupplier {

	private NavigableMap<Double, MapTile> levelMap = new TreeMap<>();
	private SimplexNoise heightNoise = new SimplexNoise(5, 0.7, 50, new Random().nextInt());
	private int mapSize;
	private List<Tile> tileTypes;
	private List<MapTile> tiles = new ArrayList<>();

	public ChunkSupplier(MapGenerator mapGenerator) {
		mapSize = mapGenerator.getSize();
		mapGenerator.foreachLayers(l -> levelMap.put((double) l.getLevel(), new EmptyTile(l.getTile())));
	}

	public Chunk get(int x, int y) {
		Chunk chunk = new Chunk(x, y);
		for (int cx = 0; cx < Chunk.CHUNK_SIZE; cx++) {
			for (int cy = 0; cy < Chunk.CHUNK_SIZE; cy++) {
				chunk.set(cx, cy,
						levelMap.ceilingEntry(heightNoise.getNoise(chunk.getOffsetX() + cx, chunk.getOffsetY() + cy))
								.getValue());
			}
		}
		return chunk;
	}
}
