package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import com.pixurvival.core.map.generator.SimplexNoise;

import lombok.Data;

@Data
public class Heightmap implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private int octave;

	private double persistence;

	private double scale;

	private transient SimplexNoise simplexNoise;

	public void initialiaze(long seed) {
		simplexNoise = new SimplexNoise(octave, persistence, scale, seed);
	}

	public double getNoise(int x, int y) {
		return simplexNoise.getNoise(x, y);
	}
}
