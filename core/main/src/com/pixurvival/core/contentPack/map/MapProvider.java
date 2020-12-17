package com.pixurvival.core.contentPack.map;

import java.util.Random;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.map.chunk.ChunkPosition;

public abstract class MapProvider extends IdentifiedElement {

	private static final long serialVersionUID = 1L;

	/**
	 * Called one time at the begin of a game
	 * 
	 * @param world
	 * @param seed
	 */
	public abstract void initialize(World world);

	/**
	 * Called before starting to build a chunk, by calling
	 * {@link #getTileAt(int, int)} for each of its tiles, then calling
	 * {@link #getStructureAt(int, int, Tile, Random)} for each of its tiles. The
	 * tiles are called in an unspecified order but must be guaranteed to be always
	 * the same for the same ChunkPosition and seed.
	 * 
	 * @param chunkPosition
	 */
	public abstract void beginChunk(long seed, ChunkPosition chunkPosition);

	public abstract Tile getTileAt(int x, int y);

	/**
	 * @param x
	 * @param y
	 * @param tile
	 *            the tile computed at position x,y with
	 *            {@link #getTileAt(int, int)}
	 * @return
	 */
	public abstract Structure getStructureAt(int x, int y, Tile tile);
}
