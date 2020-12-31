package com.pixurvival.core.map.generator;

import java.util.Random;

import com.pixurvival.core.util.MathUtils;

public class SimplexNoise {

	private SimplexNoiseOctave[] octaves;
	private float[] frequencies;
	private float[] amplitudes;

	private float scale;
	private float offsetX;
	private float offsetY;

	public SimplexNoise(int numberOfOctaves, float persistence, float scale, long seed) {

		this.scale = scale;
		octaves = new SimplexNoiseOctave[numberOfOctaves];
		frequencies = new float[numberOfOctaves];
		amplitudes = new float[numberOfOctaves];

		Random rnd = new Random(seed);
		offsetX = rnd.nextFloat() * 10_000 - 5000;
		offsetY = rnd.nextFloat() * 10_000 - 5000;

		for (int i = 0; i < numberOfOctaves; i++) {
			octaves[i] = new SimplexNoiseOctave(rnd.nextLong());
			frequencies[i] = (float) Math.pow(2, i);
			amplitudes[i] = (float) Math.pow(persistence, numberOfOctaves - i);
		}
	}

	public float getNoise(float x, float y) {

		float result = 0;

		float sx = (x + offsetX) / scale;
		float sy = (y + offsetY) / scale;
		for (int i = 0; i < octaves.length; i++) {
			result = result + octaves[i].noise(sx / frequencies[i], sy / frequencies[i]) * amplitudes[i];
		}

		return MathUtils.clamp(0.5f + result / 2, 0, 0.99999f);
	}
}