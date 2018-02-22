package com.pixurvival.core.map;

import com.pixurvival.core.contentPack.Tile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class TileAndStructure implements MapTile {

	private @NonNull Tile tile;
	private @NonNull Structure structure;

	@Override
	public boolean isSolid() {
		return tile.isSolid() || structure.isSolid();
	}

}
