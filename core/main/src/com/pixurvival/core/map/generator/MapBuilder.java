package com.pixurvival.core.map.generator;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.pixurvival.core.contentPack.MapGenerator;
import com.pixurvival.core.contentPack.Tile;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.util.ByteArray2D;
import com.pixurvival.core.util.FloatArray2D;

public class MapBuilder {

	private NavigableMap<Float, Tile> levelMap = new TreeMap<>();
	private DiamondSquareAlgorithm diamondSquareAlgorithm = new DiamondSquareAlgorithm();
	private int mapSize;

	public MapBuilder(MapGenerator map) {
		diamondSquareAlgorithm.setNoiseFactor(map.getNoiseFactor());
		mapSize = map.getSize();
		map.foreachLayers(l -> levelMap.put(l.getLevel(), l.getTile()));
	}

	public TiledMap generate(List<Tile> tileTypes) {
		FloatArray2D heightMap = new FloatArray2D(mapSize, mapSize);
		heightMap.fill(-1);
		heightMap.setHLine(0, 0);
		heightMap.setHLine(mapSize - 1, 0);
		heightMap.setVLine(0, 0);
		heightMap.setVLine(mapSize - 1, 0);
		heightMap.set(mapSize / 2, mapSize / 2, 0.6f);
		diamondSquareAlgorithm.apply(heightMap);
		ByteArray2D array = new ByteArray2D(mapSize, mapSize);
		for (int x = 0; x < mapSize; x++) {
			for (int y = 0; y < mapSize; y++) {
				array.set(x, y, levelMap.ceilingEntry(heightMap.get(x, y)).getValue().getId());
			}
		}
		array = new Smoother(2, tileTypes.size()).apply(array);
		return new TiledMap(tileTypes, array);
	}
}
