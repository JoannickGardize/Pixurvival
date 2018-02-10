package com.pixurvival.core.map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class EmptyTile implements Tile {

	private boolean solid;
	private double velocityFactor;
}
