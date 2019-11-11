package com.pixurvival.core;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GameConstants {

	public static final long FPS = 30;
	public static final int CHUNK_SIZE = 32;
	public static final int PIXEL_PER_UNIT = 8;

	public static final double EFFECT_TARGET_DISTANCE_CHECK = 16;

	public static final double PLAYER_VIEW_DISTANCE = 35;

	public static final double KEEP_ALIVE_DISTANCE = 150;
	public static final double MAX_STRUCTURE_INTERACTION_DISTANCE = 2;
	public static final double MAX_PLACE_STRUCTURE_DISTANCE = 3;

	// Calculated

	public static final double PIXEL_SIZE = 1.0 / PIXEL_PER_UNIT;

}
