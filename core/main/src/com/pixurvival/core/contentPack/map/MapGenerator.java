package com.pixurvival.core.contentPack.map;

import java.io.Serializable;
import java.util.Random;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MapGenerator extends NamedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	private Heightmap[] heightmaps;

	private Tile defaultTile;

	private TileGenerator[] tileGenerators;

	private StructureGenerator[] structureGenerators;

	public void initialize(long seed) {
		Random random = new Random(seed);
		for (Heightmap heightmap : heightmaps) {
			heightmap.initialiaze(random.nextLong());
		}
	}

	public Tile getTileAt(int x, int y) {
		for (TileGenerator tileGenerator : tileGenerators) {
			if (tileGenerator.test(x, y)) {
				return tileGenerator.getTile();
			}
		}
		return defaultTile;
	}

	public Structure getStructureAt(int x, int y, Random random) {
		for (StructureGenerator structureGenerator : structureGenerators) {
			if (structureGenerator.test(x, y)) {
				return structureGenerator.getStructure(x, y, random);
			}
		}
		return null;
	}
}
