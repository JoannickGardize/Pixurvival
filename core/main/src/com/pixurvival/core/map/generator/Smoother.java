package com.pixurvival.core.map.generator;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.util.ByteArray2D;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Smoother implements Consumer<TiledMap> {

	private static final float DIAG_FACTOR = 0.70710678118f;

	private int depth;
	private int maxValue;
	private ByteArray2D tmp = null;
	private ByteArray2D current = null;

	public Smoother(int depth, int maxValue) {
		super();
		this.depth = depth;
		this.maxValue = maxValue;
	}

	@Override
	public void accept(TiledMap tiledMap) {
		current = tiledMap.getData();
		int width = current.getWidth();
		int height = current.getHeight();
		tmp = new ByteArray2D(width, height);
		for (int i = 0; i < depth; i++) {
			tmp = new ByteArray2D(width, height);
			IntStream.range(0, width).parallel().forEach(x -> {
				float[] valueWeights = new float[maxValue];
				for (int y = 0; y < height; ++y) {
					tmp.set(x, y, smooth(current, x, y, valueWeights));
				}
			});
			ByteArray2D tmpSwap = current;
			current = tmp;
			tmp = tmpSwap;
		}
		tiledMap.setData(current);
	}

	private byte smooth(ByteArray2D t, int x, int y, float[] valueWeights) {
		Arrays.fill(valueWeights, 0);
		if (x > 0) {
			valueWeights[t.get(x - 1, y)]++;
			if (y > 0) {
				valueWeights[t.get(x - 1, y - 1)] += DIAG_FACTOR;
			}
			if (y < t.getHeight() - 1) {
				valueWeights[t.get(x - 1, y + 1)] += DIAG_FACTOR;
			}
		}
		if (x < t.getWidth() - 1) {
			valueWeights[t.get(x + 1, y)]++;
			if (y > 0) {
				valueWeights[t.get(x + 1, y - 1)] += DIAG_FACTOR;
			}
			if (y < t.getHeight() - 1) {
				valueWeights[t.get(x + 1, y + 1)] += DIAG_FACTOR;
			}
		}
		if (y > 0) {
			valueWeights[t.get(x, y - 1)]++;
		}
		if (y < t.getHeight() - 1) {
			valueWeights[t.get(x, y + 1)]++;
		}
		byte bestValue = 0;
		float bestWeight = valueWeights[0];
		for (byte i = 1; i < valueWeights.length; i++) {
			if (valueWeights[i] > bestWeight) {
				bestWeight = valueWeights[i];
				bestValue = i;
			}
		}
		return bestValue;
	}

}
