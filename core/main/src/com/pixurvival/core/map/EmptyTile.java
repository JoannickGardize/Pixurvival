package com.pixurvival.core.map;

import com.pixurvival.core.contentPack.Tile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EmptyTile implements MapTile {

	private @NonNull Tile tile;

	@Override
	public boolean isSolid() {
		return tile.isSolid();
	}
}
