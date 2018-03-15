package com.pixurvival.core.contentPack.map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;

import lombok.Getter;

@Getter
public class HeightmapCondition {

	@XmlIDREF
	@XmlAttribute(name = "name")
	private Heightmap heightmap;

	@XmlAttribute(name = "min")
	private double min;

	@XmlAttribute(name = "max")
	private double max;

	public boolean test(int x, int y) {
		double noise = heightmap.getNoise(x, y);
		return noise >= min && noise <= max;
	}
}
