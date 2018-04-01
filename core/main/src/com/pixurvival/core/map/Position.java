package com.pixurvival.core.map;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Position {

	private int x;
	private int y;

	// public void set(int x, int y) {
	// this.x = x;
	// this.y = y;
	// }
	//
	// public Position copy() {
	// return new Position(x, y);
	// }
}
