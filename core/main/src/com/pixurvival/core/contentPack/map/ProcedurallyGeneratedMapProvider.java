package com.pixurvival.core.contentPack.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementCollection;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.map.chunk.ChunkPosition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcedurallyGeneratedMapProvider extends MapProvider {

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

	private transient Random chunkRandom;

	@Override
	public void initialize(World world) {
		Random random = new Random(world.getSeed());
		for (Heightmap heightmap : heightmaps) {
			heightmap.initialiaze(random.nextLong());
		}
	}

	@Override
	public void beginChunk(long seed, ChunkPosition chunkPosition) {
		chunkRandom = new Random(seed << 32 ^ chunkPosition.getX() << 16 ^ chunkPosition.getY());

	}

	@Override
	public Tile getTileAt(int x, int y) {
		for (TileGenerator tileGenerator : tileGenerators) {
			if (tileGenerator.test(x, y)) {
				return tileGenerator.getTileAt(x, y);
			}
		}
		return defaultTile;
	}

	@Override
	public Structure getStructureAt(int x, int y, Tile tile) {
		for (StructureGenerator structureGenerator : structureGenerators) {
			if (structureGenerator.test(x, y)) {
				return structureGenerator.next(tile, chunkRandom);
			}
		}
		return null;
	}

}
