package com.pixurvival.core.map;

import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ChunkPosition {

	private int x;
	private int y;

	public static ChunkPosition fromWorldPosition(Vector2 position) {
		return new ChunkPosition(MathUtils.floor(position.getX() / GameConstants.CHUNK_SIZE), MathUtils.floor(position.getY() / GameConstants.CHUNK_SIZE));
	}

	public boolean insideSquare(Vector2 center, double halfSquareLength) {
		return x >= MathUtils.floor((center.getX() - halfSquareLength) / GameConstants.CHUNK_SIZE) && x <= MathUtils.floor((center.getX() + halfSquareLength) / GameConstants.CHUNK_SIZE)
				&& y >= MathUtils.floor((center.getY() - halfSquareLength) / GameConstants.CHUNK_SIZE) && y <= MathUtils.floor((center.getY() + halfSquareLength) / GameConstants.CHUNK_SIZE);
	}

	public String fileName() {
		return "c" + x + "_" + y;
	}

	/**
	 * Convert the world position into chunk coordinates. Return this instance
	 * if this is the result of the conversion. The purpose of this method is
	 * for performance, to avoid new allocations, in the context of entities
	 * checking every ticks their chunk positions.
	 * 
	 * @param position
	 *            the position to convert
	 * @return the ChunkPosition corresponding to the paramter position, reuse
	 *         this instance if this is the same.
	 */
	public ChunkPosition createIfDifferent(Vector2 position) {
		int positionX = MathUtils.floor(position.getX() / GameConstants.CHUNK_SIZE);
		int positionY = MathUtils.floor(position.getY() / GameConstants.CHUNK_SIZE);
		if (positionX != x || positionY != y) {
			return new ChunkPosition(positionX, positionY);
		}
		return this;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ChunkPosition)) {
			return false;
		}
		ChunkPosition otherChunkPosition = (ChunkPosition) other;
		return x == otherChunkPosition.x && y == otherChunkPosition.y;
	}

	@Override
	public int hashCode() {
		return x << 16 ^ y;
	}

	public static void forEachChunkPosition(ChunkGroupRectangle rectangle, Consumer<ChunkPosition> action) {
		int x = rectangle.getXStart();
		int yStart = rectangle.getYStart();
		int xEnd = rectangle.getXEnd();
		int yEnd = rectangle.getYEnd();
		for (; x <= xEnd; x++) {
			for (int y = yStart; y <= yEnd; y++) {
				ChunkPosition position = new ChunkPosition(x, y);
				action.accept(position);
			}
		}
	}

	public static void forEachChunkPosition(Vector2 center, double halfSquareLength, Consumer<ChunkPosition> action) {
		int x = MathUtils.floor((center.getX() - halfSquareLength) / GameConstants.CHUNK_SIZE);
		int yStart = MathUtils.floor((center.getY() - halfSquareLength) / GameConstants.CHUNK_SIZE);
		int xEnd = MathUtils.floor((center.getX() + halfSquareLength) / GameConstants.CHUNK_SIZE);
		int yEnd = MathUtils.floor((center.getY() + halfSquareLength) / GameConstants.CHUNK_SIZE);
		for (; x <= xEnd; x++) {
			for (int y = yStart; y <= yEnd; y++) {
				ChunkPosition position = new ChunkPosition(x, y);
				action.accept(position);
			}
		}
	}
}
