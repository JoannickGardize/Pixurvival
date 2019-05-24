package com.pixurvival.core.map.analytics;

import java.util.Random;

import com.pixurvival.core.util.Vector2;

import lombok.Getter;

public class StartPositionProvider {

	private double distanceStep = 256;
	private double angleStep = Math.PI / 4;

	private double initialAngle = 0;
	private @Getter int step = 0;

	public StartPositionProvider(Random random) {
		initialAngle = random.nextDouble() * Math.PI * 2;
	}

	public Vector2 next() {
		Vector2 result = Vector2.fromEuclidean(step * distanceStep, initialAngle + step * angleStep);
		step++;
		return result;
	}
}
