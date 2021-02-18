package com.pixurvival.core.map;

import com.pixurvival.core.contentPack.map.Tile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class TileAndStructure implements MapTile {

	private @NonNull Tile tileDefinition;
	private @NonNull StructureEntity structure;

	@Override
	public boolean isSolid() {
		return tileDefinition.isSolid() || structure.getDefinition().isSolid();
	}

}
