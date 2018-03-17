package com.pixurvival.core.contentPack.map;

import java.util.Random;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.RefAdapter;

import lombok.Getter;

@Getter
public class MapGenerator extends NamedElement {

	@XmlElementWrapper(name = "heightmaps")
	@XmlElement(name = "heightmap")
	private Heightmap[] heightmaps;

	@XmlElement(name = "defaultTile")
	@XmlJavaTypeAdapter(RefAdapter.TileRefAdapter.class)
	private Tile defaultTile;

	@XmlElementWrapper(name = "tileGenerators")
	@XmlElement(name = "tileGenerator")
	private TileGenerator[] tileGenerators;

	@XmlElementWrapper(name = "structureGenerators")
	@XmlElement(name = "structureGenerator")
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
