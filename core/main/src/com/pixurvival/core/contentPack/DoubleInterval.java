package com.pixurvival.core.contentPack;

import java.io.Serializable;
import java.util.Random;

import lombok.Data;

@Data
public class DoubleInterval implements Serializable {

	private static final long serialVersionUID = 1L;

	private double min;
	private double max;

	public double next(Random random) {
		return random.nextDouble() * (max - min) + min;
	}
}
