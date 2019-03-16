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
}
