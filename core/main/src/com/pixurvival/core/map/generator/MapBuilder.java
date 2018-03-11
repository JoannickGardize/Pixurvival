package com.pixurvival.core.map.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.pixurvival.core.contentPack.map.MapGenerator;
import com.pixurvival.core.contentPack.map.Tile;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.util.ByteArray2D;
import com.pixurvival.core.util.FloatArray2D;

public class MapBuilder {

	private NavigableMap<Float, Tile> levelMap = new TreeMap<>();
	private List<UnaryOperator<FloatArray2D>> heightmapProcessors = new ArrayList<>();
	private List<Consumer<TiledMap>> postProcessors = new ArrayList<>();
	private int mapSize;
	private List<Tile> tileTypes;

	public MapBuilder(MapGenerator mapGenerator, List<Tile> tileTypes) {
		this.tileTypes = tileTypes;
		DiamondSquareAlgorithm algorithm = new DiamondSquareAlgorithm();
		algorithm.setNoiseFactor(mapGenerator.getNoiseFactor());
		heightmapProcessors.add(new IslandBase());
		heightmapProcessors.add(algorithm);
		postProcessors.add(new Smoother(2, tileTypes.size()));
		mapSize = mapGenerator.getSize();
		mapGenerator.foreachLayers(l -> levelMap.put(l.getLevel(), l.getTile()));
	}

	public TiledMap generate() {
		FloatArray2D heightmap = new FloatArray2D(mapSize, mapSize);
		for (UnaryOperator<FloatArray2D> processor : heightmapProcessors) {
			heightmap = processor.apply(heightmap);
		}
		ByteArray2D array = new ByteArray2D(mapSize, mapSize);
		for (int x = 0; x < mapSize; x++) {
			for (int y = 0; y < mapSize; y++) {
				array.set(x, y, levelMap.ceilingEntry(heightmap.get(x, y)).getValue().getId());
			}
		}
		TiledMap map = new TiledMap(tileTypes, array);
		postProcessors.forEach(p -> p.accept(map));
		return map;
	}
}
