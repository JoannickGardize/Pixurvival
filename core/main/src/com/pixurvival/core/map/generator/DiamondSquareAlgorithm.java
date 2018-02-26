package com.pixurvival.core.map.generator;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;

import com.pixurvival.core.util.FloatArray2D;

import lombok.Setter;

public class DiamondSquareAlgorithm implements UnaryOperator<FloatArray2D> {

	private Random random = new Random();

	private @Setter float noiseFactor = 3;

	@Override
	public FloatArray2D apply(FloatArray2D t) {
		if (t.getWidth() != t.getHeight() || ((t.getWidth() - 1) & (t.getWidth() - 2)) != 0 || t.getWidth() < 3) {
			throw new IllegalArgumentException("the FloatArray2D must be a square with a width of 2^n + 1.");
		}
		int i = t.getWidth() - 1;
		float halfWidth = t.getWidth() / 2;
		while (i > 1) {
			int id = i / 2;
			for (int x = id; x < t.getWidth(); x += i) {
				for (int y = id; y < t.getWidth(); y += i) {
					if (t.get(x, y) != -1) {
						continue;
					}
					float value = (t.get(x - id, y - id) + t.get(x - id, y + id) + t.get(x + id, y + id)
							+ t.get(x + id, y - id)) / 4f + randomNoise(id, halfWidth);
					t.set(x, y, bound(value));
				}
			}
			int shift = 0;
			for (int x = 0; x < t.getWidth(); x += id) {
				shift = shift == 0 ? id : 0;
				for (int y = shift; y < t.getWidth(); y += i) {
					if (t.get(x, y) != -1) {
						continue;
					}
					float sum = 0;
					int n = 0;
					if (x >= id) {
						sum += t.get(x - id, y);
						n++;
					}
					if (x + id < t.getWidth()) {
						sum += t.get(x + id, y);
						n++;
					}
					if (y >= id) {
						sum = sum + t.get(x, y - id);
						n++;
					}
					if (y + id < t.getWidth()) {
						sum = sum + t.get(x, y + id);
						n++;
					}
					t.set(x, y, bound(sum / n + randomNoise(id, halfWidth)));
				}
			}
			i = id;
		}
		return t;
	}

	private float bound(float value) {
		if (value > 1) {
			return 1;
		} else if (value < 0) {
			return 0;
		} else {
			return value;
		}
	}

	private float randomNoise(float distance, float halfWidth) {
		return (random.nextFloat() * 2 - 1) * distance / 1000 * noiseFactor;
	}

	public static void main(String[] args) throws IOException {
		int width = 2049;
		FloatArray2D array = new FloatArray2D(width, width);
		array.fill(-1);
		array.setHLine(0, 0);
		System.out.println(array.get(1000, 0));
		array.setHLine(width - 1, 0);
		array.setVLine(0, 0);
		array.setVLine(width - 1, 0);
		array.set(width / 2, width / 2, 0.6f);
		new DiamondSquareAlgorithm().apply(array);

		NavigableMap<Float, Integer> rangeMap = new TreeMap<>();
		rangeMap.put(0.25f, Color.BLACK.getRGB());
		rangeMap.put(0.3f, Color.BLUE.getRGB());
		rangeMap.put(0.35f, Color.yellow.getRGB());
		rangeMap.put(0.8f, Color.GREEN.getRGB());
		rangeMap.put(0.9f, Color.gray.getRGB());
		rangeMap.put(1f, Color.darkGray.getRGB());

		BufferedImage b = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < width; y++) {
				b.setRGB(x, y, rangeMap.ceilingEntry(array.get(x, y)).getValue());
			}
		}

		ImageIO.write(b, "png", new File("test.png"));
	}
}
