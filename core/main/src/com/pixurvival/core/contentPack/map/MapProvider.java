package com.pixurvival.core.contentPack.map;

import java.util.Random;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.structure.Structure;

public abstract class MapProvider extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	public abstract Tile getTileAt(int x, int y);

	/**
	 * @param x
	 * @param y
	 * @param tile
	 *            the tile computed with {@link #getTileAt(int, int)}
	 * @param random
	 * @return
	 */
	public abstract Structure getStructureAt(int x, int y, Tile tile, Random random);
}
