package com.pixurvival.core.contentPack.map;

import java.io.Serializable;

import lombok.Data;

@Data
public class TileGenerator implements Serializable {

	private static final long serialVersionUID = 1L;

	private HeightmapCondition[] heightmapConditions;

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
