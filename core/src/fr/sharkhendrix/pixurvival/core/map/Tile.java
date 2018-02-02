package fr.sharkhendrix.pixurvival.core.map;

public interface Tile {

	static final Tile DEEP_WATER = new EmptyTile(true, 0);
	static final Tile WATER = new EmptyTile(false, 0.3);
	static final Tile SAND = new EmptyTile(false, 1);
	static final Tile GRASS = new EmptyTile(false, 1);
	static final Tile ROCK = new EmptyTile(false, 0.8);
	static final Tile MONTAIN = new EmptyTile(true, 0);

	boolean isSolid();

	double getVelocityFactor();
}
