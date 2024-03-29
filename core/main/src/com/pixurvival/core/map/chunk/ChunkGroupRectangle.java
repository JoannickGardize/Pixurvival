package com.pixurvival.core.map.chunk;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

@Data
@NoArgsConstructor
public class ChunkGroupRectangle {

    private int xStart = Integer.MAX_VALUE;
    private int xEnd = Integer.MAX_VALUE;
    private int yStart = Integer.MAX_VALUE;
    private int yEnd = Integer.MAX_VALUE;

    public ChunkGroupRectangle(Vector2 center, float halfSquareLength) {
        set(center, halfSquareLength);
    }

    public void reset() {
        xStart = Integer.MAX_VALUE;
        xEnd = Integer.MAX_VALUE;
        yStart = Integer.MAX_VALUE;
        yEnd = Integer.MAX_VALUE;
    }

    /**
     * Set this ChunkGroupRectangle as a square defined by parameters.
     *
     * @param center           the center point of the square, in world space
     * @param halfSquareLength The half length of the square, in world space
     * @return true if the rectangle has changed
     */
    public boolean set(Vector2 center, float halfSquareLength) {
        int newXStart = MathUtils.floor((center.getX() - halfSquareLength) / GameConstants.CHUNK_SIZE);
        int newXEnd = MathUtils.floor((center.getX() + halfSquareLength) / GameConstants.CHUNK_SIZE);
        int newYStart = MathUtils.floor((center.getY() - halfSquareLength) / GameConstants.CHUNK_SIZE);
        int newYEnd = MathUtils.floor((center.getY() + halfSquareLength) / GameConstants.CHUNK_SIZE);
        if (xStart != newXStart || xEnd != newXEnd || yStart != newYStart || yEnd != newYEnd) {
            xStart = newXStart;
            xEnd = newXEnd;
            yStart = newYStart;
            yEnd = newYEnd;
            return true;
        }
        return false;
    }

    public void forEachChunkPosition(Consumer<ChunkPosition> action) {
        for (int x = xStart; x <= xEnd; x++) {
            for (int y = yStart; y <= yEnd; y++) {
                action.accept(new ChunkPosition(x, y));
            }
        }
    }

    public boolean contains(ChunkPosition position) {
        return position.getX() >= xStart && position.getX() <= xEnd && position.getY() >= yStart && position.getY() <= yEnd;
    }
}
