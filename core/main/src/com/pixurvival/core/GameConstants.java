package com.pixurvival.core;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GameConstants {

	public static final int CHUNK_SIZE = 32;
	public static final int PIXEL_PER_UNIT = 8;

	public static final double PLAYER_VIEW_DISTANCE = 45;

	public static final double KEEP_ALIVE_DISTANCE = 150;
	public static final double MAX_HARVEST_DISTANCE = 2;
	public static final double MAX_PLACE_STRUCTURE_DISTANCE = 3;

	// Calculated

	public static final double PLAYER_ENTITY_VIEW_DISTANCE = Math.sqrt(2) * PLAYER_VIEW_DISTANCE;
	public static final double PIXEL_SIZE = 1.0 / PIXEL_PER_UNIT;
	public static final int PLAYER_CHUNK_VIEW_DISTANCE = (int) Math.ceil(PLAYER_ENTITY_VIEW_DISTANCE / CHUNK_SIZE);
	public static final int KEEP_ALIVE_CHUNK_VIEW_DISTANCE = (int) Math.ceil(KEEP_ALIVE_DISTANCE / CHUNK_SIZE);

}
