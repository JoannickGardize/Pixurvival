package com.pixurvival.core.map;

import com.pixurvival.core.GameConstants;
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
		int newXStart = (int) Math.floor((center.getX() - halfSquareLength) / GameConstants.CHUNK_SIZE);
		int newXEnd = (int) Math.floor((center.getX() + halfSquareLength) / GameConstants.CHUNK_SIZE);
		int newYStart = (int) Math.floor((center.getY() - halfSquareLength) / GameConstants.CHUNK_SIZE);
		int newYEnd = (int) Math.floor((center.getY() + halfSquareLength) / GameConstants.CHUNK_SIZE);
		if (xStart != newXStart || xEnd != newXEnd || yStart != newYStart || yEnd != newYEnd) {
			xStart = newXStart;
			xEnd = newXEnd;
			yStart = newYStart;
			yEnd = newYEnd;
			return true;
		}
		return false;
	}
}
