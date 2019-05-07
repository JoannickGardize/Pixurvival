package com.pixurvival.core.map;

import java.util.function.Consumer;

import com.pixurvival.core.GameConstants;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ChunkPosition {

	public enum NeighbourType {
		EAST,
		WEST,
		NORTH,
		SOUTH,
		NORTH_EAST,
		NORTH_WEST,
		SOUTH_EAST,
		SOUTH_WEST;
	}

	private int x;
	private int y;

	public static ChunkPosition fromWorldPosition(Vector2 position) {
		return new ChunkPosition((int) Math.floor(position.getX() / GameConstants.CHUNK_SIZE), (int) Math.floor(position.getY() / GameConstants.CHUNK_SIZE));
	}

	public boolean insideSquare(Vector2 center, double halfSquareLength) {
		return x >= (int) Math.floor((center.getX() - halfSquareLength) / GameConstants.CHUNK_SIZE) && x <= (int) Math.floor((center.getX() + halfSquareLength) / GameConstants.CHUNK_SIZE)
				&& y >= (int) Math.floor((center.getY() - halfSquareLength) / GameConstants.CHUNK_SIZE) && y <= (int) Math.floor((center.getY() + halfSquareLength) / GameConstants.CHUNK_SIZE);
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
		int positionX = (int) Math.floor(position.getX() / GameConstants.CHUNK_SIZE);
		int positionY = (int) Math.floor(position.getY() / GameConstants.CHUNK_SIZE);
		if (positionX != x || positionY != y) {
			return new ChunkPosition(positionX, positionY);
		}
		return this;
	}

	/**
	 * Return the Neighbour type of the ChunkPosition for this ChunkPosition.
	 * 
	 * @param other
	 *            The position relative to this instance
	 * @return The Neightbour type, or null if not a neightbour or other is this
	 *         instance
	 * @deprecated Use instead ChunkGroupRectangle & ChunkGroupChangeHelper
	 */
	@Deprecated
	public NeighbourType neighbourTypeOf(ChunkPosition other) {
		int dx = other.x - x;
		int dy = other.y - y;
		switch (dx) {
		case 0:
			switch (dy) {
			case 1:
				return NeighbourType.NORTH;
			case -1:
				return NeighbourType.SOUTH;
			default:
				return null;
			}
		case 1:
			switch (dy) {
			case 0:
				return NeighbourType.EAST;
			case 1:
				return NeighbourType.NORTH_EAST;
			case -1:
				return NeighbourType.SOUTH_EAST;
			default:
				return null;
			}
		case -1:
			switch (dy) {
			case 0:
				return NeighbourType.WEST;
			case 1:
				return NeighbourType.NORTH_WEST;
			case -1:
				return NeighbourType.SOUTH_WEST;
			default:
				return null;
			}
		default:
			return null;
		}
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
		int x = (int) Math.floor((center.getX() - halfSquareLength) / GameConstants.CHUNK_SIZE);
		int yStart = (int) Math.floor((center.getY() - halfSquareLength) / GameConstants.CHUNK_SIZE);
		int xEnd = (int) Math.floor((center.getX() + halfSquareLength) / GameConstants.CHUNK_SIZE);
		int yEnd = (int) Math.floor((center.getY() + halfSquareLength) / GameConstants.CHUNK_SIZE);
		for (; x <= xEnd; x++) {
			for (int y = yStart; y <= yEnd; y++) {
				ChunkPosition position = new ChunkPosition(x, y);
				action.accept(position);
			}
		}
	}
}
