package com.pixurvival.core.contentPack.sprite;

import com.pixurvival.core.Direction;

import java.util.EnumMap;
import java.util.Map;

public enum ActionAnimation {
    MOVE_RIGHT,
    MOVE_UP,
    MOVE_LEFT,
    MOVE_DOWN,
    STAND_RIGHT,
    STAND_UP,
    STAND_LEFT,
    STAND_DOWN,
    WORK_RIGHT,
    WORK_UP,
    WORK_DOWN,
    WORK_LEFT,
    BEFORE_DEFAULT,
    DEFAULT,
    HARVESTED;

    private static Map<Direction, ActionAnimation> moveByDirection = new EnumMap<>(Direction.class);
    private static Map<Direction, ActionAnimation> standByDirection = new EnumMap<>(Direction.class);
    private static Map<Direction, ActionAnimation> workByDirection = new EnumMap<>(Direction.class);

    static {
        moveByDirection.put(Direction.EAST, MOVE_RIGHT);
        moveByDirection.put(Direction.NORTH, MOVE_UP);
        moveByDirection.put(Direction.SOUTH, MOVE_DOWN);
        moveByDirection.put(Direction.WEST, MOVE_LEFT);

        standByDirection.put(Direction.EAST, STAND_RIGHT);
        standByDirection.put(Direction.NORTH, STAND_UP);
        standByDirection.put(Direction.SOUTH, STAND_DOWN);
        standByDirection.put(Direction.WEST, STAND_LEFT);

        workByDirection.put(Direction.EAST, WORK_RIGHT);
        workByDirection.put(Direction.NORTH, WORK_UP);
        workByDirection.put(Direction.SOUTH, WORK_DOWN);
        workByDirection.put(Direction.WEST, WORK_LEFT);
    }

    public static ActionAnimation getMoveFromDirection(Direction direction) {
        return moveByDirection.get(direction);
    }

    public static ActionAnimation getStandFromDirection(Direction direction) {
        return standByDirection.get(direction);
    }

    public static ActionAnimation getWorkFromDirection(Direction direction) {
        return workByDirection.get(direction);
    }
}