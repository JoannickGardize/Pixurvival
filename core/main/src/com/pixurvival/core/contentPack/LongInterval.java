package com.pixurvival.core.contentPack;

import java.util.Random;

import lombok.Data;

@Data
public class LongInterval {

	private long min;

	private long max;

	public long next(Random random) {
		return random.nextInt() * (max - min) + min;
	}
}
