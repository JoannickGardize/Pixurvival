package com.pixurvival.core.contentPack.map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pixurvival.core.contentPack.RefAdapter;

import lombok.Getter;

@Getter
public class TileGenerator {

	@XmlElement(name = "heightmapCondition")
	private HeightmapCondition[] heightmapConditions;

	@XmlElement(name = "tile")
	@XmlJavaTypeAdapter(RefAdapter.TileRefAdapter.class)
	private Tile tile;

	public boolean test(int x, int y) {
		for (HeightmapCondition h : heightmapConditions) {
			if (!h.test(x, y)) {
				return false;
			}
		}
		return true;
	}
}
