package com.pixurvival.core.contentPack.map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import com.pixurvival.core.map.generator.SimplexNoise;

import lombok.Getter;

@Getter
public class Heightmap {

	@XmlID
	@XmlAttribute(name = "name")
	private String name;

	@XmlAttribute(name = "octave")
	private int octave;

	@XmlAttribute(name = "persistence")
	private double persistence;

	@XmlAttribute(name = "scale")
	private double scale;

	private SimplexNoise simplexNoise;

	public void initialiaze(long seed) {
		simplexNoise = new SimplexNoise(octave, persistence, scale, seed);
	}

	public double getNoise(int x, int y) {
		return simplexNoise.getNoise(x, y);
	}
}
