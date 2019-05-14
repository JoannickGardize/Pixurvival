package com.pixurvival.core.contentPack.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementCollection;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapGenerator extends IdentifiedElement implements Serializable {

	private static final long serialVersionUID = 1L;

	@Valid
	@ElementCollection(value = Heightmap.class, isRoot = false)
	private List<Heightmap> heightmaps = new ArrayList<>();

	@Required
	@ElementReference
	private Tile defaultTile;

	@Valid
	private List<TileGenerator> tileGenerators = new ArrayList<>();

	@Valid
	private List<StructureGenerator> structureGenerators = new ArrayList<>();

	public void initialize(long seed) {
		Random random = new Random(seed);
		for (Heightmap heightmap : heightmaps) {
			heightmap.initialiaze(random.nextLong());
		}
	}

	public Tile getTileAt(int x, int y) {
		for (TileGenerator tileGenerator : tileGenerators) {
			if (tileGenerator.test(x, y)) {
				return tileGenerator.getTileAt(x, y);
			}
		}
		return defaultTile;
	}

	public Structure getStructureAt(int x, int y, Tile tile, Random random) {
		for (StructureGenerator structureGenerator : structureGenerators) {
			if (structureGenerator.test(x, y)) {
				return structureGenerator.next(tile, random);
			}
		}
		return null;
	}

}
