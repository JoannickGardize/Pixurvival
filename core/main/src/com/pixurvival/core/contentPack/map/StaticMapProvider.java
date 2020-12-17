package com.pixurvival.core.contentPack.map;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.map.chunk.ChunkPosition;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaticMapProvider extends MapProvider {

	private static final long serialVersionUID = 1L;

	@ElementReference
	private Tile defaultTile;

	@Valid
	private ImageMapping<Tile> tileMap = new ImageMapping<>();

	@Valid
	private ImageMapping<Structure> structureMap = new ImageMapping<>();

	@Override
	public void initialize() {
		tileMap.initializeMapping();
		structureMap.initializeMapping();
	}

	@Override
	public void initialize(World world) {
		tileMap.initializeImage(world.getContentPack(), getTilesImageResourceName());
		structureMap.initializeImage(world.getContentPack(), getStructuresImageResourceName());
	}

	@Override
	public void beginChunk(long seed, ChunkPosition chunkPosition) {
		// Nothing to do
	}

	@Override
	public Tile getTileAt(int x, int y) {
		Tile result = tileMap.getElementAt(x, y);
		return result == null ? defaultTile : result;
	}

	@Override
	public Structure getStructureAt(int x, int y, Tile tile) {
		return structureMap.getElementAt(x, y);
	}

	public String getTilesImageResourceName() {
		return getTilesImageResourceName(getName());
	}

	public String getStructuresImageResourceName() {
		return getStructuresImageResourceName(getName());
	}

	public static String getTilesImageResourceName(String name) {
		return "maps/" + name + "_tiles.png";
	}

	public static String getStructuresImageResourceName(String name) {
		return "maps/" + name + "_structures.png";
	}
}
