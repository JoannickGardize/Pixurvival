package com.pixurvival.core;

public class GameConstants {

	public static final int CHUNK_SIZE = 32;
	public static final int PIXEL_PER_UNIT = 8;
	public static final double PLAYER_VIEW_DISTANCE = 45;

	// Calculated

	public static final double PIXEL_SIZE = 1.0 / PIXEL_PER_UNIT;
	public static final int PLAYER_CHUNK_VIEW_DISTANCE = (int) Math.ceil(PLAYER_VIEW_DISTANCE / CHUNK_SIZE);

}
