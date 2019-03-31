package com.pixurvival.core.util;

import java.util.Random;

public class WorldRandom extends Random {

	private static final long serialVersionUID = 1L;

	public double nextAngle() {
		return nextDouble() * Math.PI * 2;
	}
}
