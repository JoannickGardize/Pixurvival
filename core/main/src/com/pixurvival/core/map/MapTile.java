package com.pixurvival.core.map;

import com.pixurvival.core.contentPack.Tile;

public interface MapTile {

	Tile getTile();

	default Structure getStructure() {
		return null;
	}

	boolean isSolid();
}
