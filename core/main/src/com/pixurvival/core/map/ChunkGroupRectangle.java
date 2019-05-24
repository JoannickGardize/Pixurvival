package com.pixurvival.core.map;

import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;

import lombok.Data;

@Data
public class ChunkGroupRectangle {

	private int xStart;
	private int xEnd;
	private int yStart;
	private int yEnd;

	/**
	 * Set this ChunkGroupRectangle as a square defined by parameters.
	 * 
	 * @param center
	 *            the center point of the square, in world space
	 * @param halfSquareLength
	 *            The half length of the square, in world space
	 * @return true if the rectangle has changed
	 */
	public boolean set(Vector2 center, double halfSquareLength) {
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
}
