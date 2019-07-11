package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.util.Random;

import lombok.Data;

@Data
public class LongInterval implements Serializable {

	private static final long serialVersionUID = 1L;

	private long min;

	private long max;

	public long next(Random random) {
		int randomRange = (int) (max - min);
		if (randomRange > 0) {
			return random.nextInt(randomRange) + min;
		} else {
			return min;
		}
	}
}
