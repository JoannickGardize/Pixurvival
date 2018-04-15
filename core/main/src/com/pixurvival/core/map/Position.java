package com.pixurvival.core.map;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Position {

	private int x;
	private int y;

	public boolean insideSquare(Position other, int size) {
		int dx = Math.abs(x - other.x);
		int dy = Math.abs(y - other.y);
		return dx <= size && dy <= size;
	}

	public String fileName() {
		return x + "_" + y;
	}
}
