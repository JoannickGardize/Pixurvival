package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import lombok.Data;

@Data
public class HeightmapCondition implements Serializable {

	private static final long serialVersionUID = 1L;

	private Heightmap heightmap;

	private double min;

	private double max;

	public boolean test(int x, int y) {
		double noise = heightmap.getNoise(x, y);
		return noise >= min && noise < max;
	}
}
