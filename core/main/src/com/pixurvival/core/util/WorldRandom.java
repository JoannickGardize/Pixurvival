package com.pixurvival.core.util;

import java.util.Random;

public class WorldRandom extends Random {

	private static final long serialVersionUID = 1L;

	public double nextAngle() {
		return nextDouble() * Math.PI * 2;
	}

	public Vector2 nextVector2InRectangle(double x, double y, double width, double height) {
		return new Vector2(nextDouble() * width, nextDouble() * height).add(x, y);
	}

	public Vector2 nextVector2InCircle(Vector2 center, double radius) {
		return Vector2.fromEuclidean(nextDouble() * radius, nextAngle()).add(center);
	}
}
