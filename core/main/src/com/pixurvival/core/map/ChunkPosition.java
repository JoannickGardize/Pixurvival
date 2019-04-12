package com.pixurvival.core.map;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ChunkPosition {

	private int x;
	private int y;

	public boolean insideSquare(ChunkPosition other, int halfSize) {
		int dx = Math.abs(x - other.x);
		int dy = Math.abs(y - other.y);
		return dx <= halfSize && dy <= halfSize;
	}

	public String fileName() {
		return "c" + x + "_" + y;
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
}
