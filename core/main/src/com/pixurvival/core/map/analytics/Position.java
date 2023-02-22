package com.pixurvival.core.map.analytics;

import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Position {

    private int x;
    private int y;

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position copy() {
        return new Position(x, y);
    }

    public void addX(int toAdd) {
        x += toAdd;
    }

    public void addY(int toAdd) {
        y += toAdd;
    }

    public int getXGroup(int interval) {
        return x < 0 ? (x + 1) / interval - 1 : x / interval;
    }

    public int getYGroup(int interval) {
        return y < 0 ? (y + 1) / interval - 1 : y / interval;
    }

    public boolean searchPositionInSquare(int distance, PositionPredicate action) {
        for (int xi = x - distance; xi <= x + distance; xi++) {
            if (action.test(xi, y + distance) || action.test(xi, y - distance)) {
                return true;
            }
        }
        for (int yi = y - distance + 1; yi < y + distance; yi++) {
            if (action.test(x - distance, yi) || action.test(x + distance, yi)) {
                return true;
            }
        }
        return false;
    }

    public Vector2 toVector2(int scale) {
        return new Vector2(x * scale + 0.5f, y * scale + 0.5f);
    }

    public static Position fromWorldPosition(Vector2 position, int interval) {
        return new Position(MathUtils.floor(position.getX() / interval) * interval, MathUtils.floor(position.getY() / interval) * interval);
    }

    public static Position relativeTo(Position origin, int dx, int dy) {
        return new Position(origin.x + dx, origin.y + dy);
    }
}
