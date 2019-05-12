package com.pixurvival.core.map.generator;

import java.util.Random;

import com.pixurvival.core.util.MathUtils;

public class SimplexNoise {

	private SimplexNoiseOctave[] octaves;
	private double[] frequencies;
	private double[] amplitudes;

	private double scale;
	private double offsetX;
	private double offsetY;

	public SimplexNoise(int numberOfOctaves, double persistence, double scale, long seed) {

		this.scale = scale;
		octaves = new SimplexNoiseOctave[numberOfOctaves];
		frequencies = new double[numberOfOctaves];
		amplitudes = new double[numberOfOctaves];

		Random rnd = new Random(seed);
		offsetX = rnd.nextDouble() * 10_000 - 5000;
		offsetY = rnd.nextDouble() * 10_000 - 5000;

		for (int i = 0; i < numberOfOctaves; i++) {
			octaves[i] = new SimplexNoiseOctave(rnd.nextLong());
			frequencies[i] = Math.pow(2, i);
			amplitudes[i] = Math.pow(persistence, numberOfOctaves - i);
		}
	}

	public double getNoise(double x, double y) {

		double result = 0;

		double sx = (x + offsetX) / scale;
		double sy = (y + offsetY) / scale;
		for (int i = 0; i < octaves.length; i++) {
			result = result + octaves[i].noise(sx / frequencies[i], sy / frequencies[i]) * amplitudes[i];
		}

		return MathUtils.clamp(0.5 + result / 2, 0, 0.99999);

	}
}