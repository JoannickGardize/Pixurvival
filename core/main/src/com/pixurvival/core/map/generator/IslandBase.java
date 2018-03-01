package com.pixurvival.core.map.generator;

import java.util.function.UnaryOperator;

import com.pixurvival.core.util.FloatArray2D;

public class IslandBase implements UnaryOperator<FloatArray2D> {

	@Override
	public FloatArray2D apply(FloatArray2D heightmap) {
		heightmap.fill(-1);
		heightmap.setHLine(0, 0);
		heightmap.setHLine(heightmap.getHeight() - 1, 0);
		heightmap.setVLine(0, 0);
		heightmap.setVLine(heightmap.getWidth() - 1, 0);
		heightmap.set(heightmap.getWidth() / 2, heightmap.getHeight() / 2, 0.6f);
		return heightmap;
	}

}
