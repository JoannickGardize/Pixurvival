package com.pixurvival.core;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GameConstants {

	public static final long FPS = 30;
	public static final int CHUNK_SIZE = 32;
	public static final int PIXEL_PER_UNIT = 8;

	public static final float EFFECT_TARGET_DISTANCE_CHECK = 16;

	public static final float PLAYER_VIEW_DISTANCE = 35;

	public static final float KEEP_ALIVE_DISTANCE = 80;
	public static final float MAX_STRUCTURE_INTERACTION_DISTANCE = 2;
	public static final float MAX_PLACE_STRUCTURE_DISTANCE = 3;

	// Calculated

	public static final float PIXEL_SIZE = 1f / PIXEL_PER_UNIT;

	public static final long CLIENT_STREAM_INTERVAL = 66;

	public static final float HUNGER_DAMAGE_PER_SECOND = 10;

}
