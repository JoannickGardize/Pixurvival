package com.pixurvival.core.util;

import java.util.Random;

public class WorldRandom extends Random {

	private static final long serialVersionUID = 1L;

	public float nextAngle() {
		return nextFloat() * (float) Math.PI * 2 - (float) Math.PI;
	}

	/**
	 * @param halfRange
	 * @return random value in range [-range ; range]
	 */
	public float nextAngle(float halfRange) {
		if (halfRange == 0) {
			// Avoid the cost of nextFloat()
			return 0;
		} else {
			return nextFloat() * halfRange * 2 - halfRange;
		}
	}

	public Vector2 nextVector2InRectangle(float x, float y, float width, float height) {
		return new Vector2(nextFloat() * width, nextFloat() * height).add(x, y);
	}

	public Vector2 nextVector2InCircle(Vector2 center, float radius) {
		return Vector2.fromEuclidean(nextFloat() * radius, nextAngle()).add(center);
	}
}
