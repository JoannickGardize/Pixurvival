package com.pixurvival.core.contentPack.sprite;

import java.util.EnumMap;
import java.util.Map;

import com.pixurvival.core.Direction;

public enum ActionAnimation {
	MOVE_RIGHT,
	MOVE_UP,
	MOVE_LEFT,
	MOVE_DOWN,
	STAND_RIGHT,
	STAND_UP,
	STAND_LEFT,
	STAND_DOWN,
	NONE,
	HARVESTED;

	private static Map<Direction, ActionAnimation> moveByDirection = new EnumMap<>(Direction.class);
	private static Map<Direction, ActionAnimation> standByDirection = new EnumMap<>(Direction.class);

	static {
		moveByDirection.put(Direction.EAST, MOVE_RIGHT);
		moveByDirection.put(Direction.NORTH, MOVE_UP);
		moveByDirection.put(Direction.SOUTH, MOVE_DOWN);
		moveByDirection.put(Direction.WEST, MOVE_LEFT);

		standByDirection.put(Direction.EAST, STAND_RIGHT);
		standByDirection.put(Direction.NORTH, STAND_UP);
		standByDirection.put(Direction.SOUTH, STAND_DOWN);
		standByDirection.put(Direction.WEST, STAND_LEFT);
	}

	public static ActionAnimation getMoveFromDirection(Direction direction) {
		return moveByDirection.get(direction);
	}

	public static ActionAnimation getStandFromDirection(Direction direction) {
		return standByDirection.get(direction);
	}
}