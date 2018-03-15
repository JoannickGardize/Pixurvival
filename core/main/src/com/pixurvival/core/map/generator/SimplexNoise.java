package com.pixurvival.core.map.generator;

import java.util.Random;

import com.pixurvival.core.util.MathUtils;

public class SimplexNoise {

	private SimplexNoiseOctave[] octaves;
	private double[] frequencys;
	private double[] amplitudes;

	private double scale;

	public SimplexNoise(int numberOfOctaves, double persistence, double scale, long seed) {
		this.scale = scale;

		octaves = new SimplexNoiseOctave[numberOfOctaves];
		frequencys = new double[numberOfOctaves];
		amplitudes = new double[numberOfOctaves];

		Random rnd = new Random(seed);

		for (int i = 0; i < numberOfOctaves; i++) {
			octaves[i] = new SimplexNoiseOctave(rnd.nextLong());

			frequencys[i] = Math.pow(2, i);
			amplitudes[i] = Math.pow(persistence, numberOfOctaves - i);

		}

	}

	public double getNoise(double x, double y) {

		double result = 0;

		double sx = x / scale;
		double sy = y / scale;
		for (int i = 0; i < octaves.length; i++) {
			// double frequency = Math.pow(2,i);
			// double amplitude = Math.pow(persistence,octaves.length-i);
			result = result + octaves[i].noise(sx / frequencys[i], sy / frequencys[i]) * amplitudes[i];
		}

		return MathUtils.clamp(0.5 + result / 2, 0, 0.99999);

	}
}