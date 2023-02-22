package com.pixurvival.core.map.analytics;

import lombok.Getter;

@Getter
public enum CardinalDirection {
    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);

    static {
        for (CardinalDirection value : values()) {
            value.next = values()[Math.floorMod(value.ordinal() + 1, 4)];
            value.previous = values()[Math.floorMod(value.ordinal() - 1, 4)];
            value.opposite = values()[Math.floorMod(value.ordinal() + 2, 4)];
        }
    }

    private int normalX;
    private int normalY;
    private CardinalDirection next;
    private CardinalDirection previous;
    private CardinalDirection opposite;

    private CardinalDirection(int normalX, int normalY) {
        this.normalX = normalX;
        this.normalY = normalY;
    }
}