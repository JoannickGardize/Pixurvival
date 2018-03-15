package com.pixurvival.core.map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
