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
		return random.nextInt() * (max - min) + min;
	}
}
