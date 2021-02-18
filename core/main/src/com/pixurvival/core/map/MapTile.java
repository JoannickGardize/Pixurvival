package com.pixurvival.core.map;

import com.pixurvival.core.contentPack.map.Tile;

public interface MapTile {

	Tile getTileDefinition();

	default StructureEntity getStructure() {
		return null;
	}

	boolean isSolid();
}
