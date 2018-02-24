package com.pixurvival.core.util;

import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
public class FloatArray2D {
	@Getter
	private int width;
	@Getter
	private int height;
	private float[] data;

	public FloatArray2D(int width, int height) {
		this.width = width;
		this.height = height;

		data = new float[width * height];
	}

	public float get(int x, int y) {
		return data[x + y * width];
	}

	public void set(int x, int y, float element) {
		data[x + y * width] = element;
	}

	public void fill(float element) {
		Arrays.fill(data, element);
	}

	/**
	 * Fill this Array 2D with the given Array 2D. This method is super
	 * efficient, using {@link System#arraycopy(Object, int, Object, int, int)}
	 * for each rows.
	 * 
	 * @param x
	 *            The start X position where the rectangle will be copied.
	 * @param y
	 *            The start Y position where the rectangle will be copied.
	 * @param rect
	 *            The rectangle to copy. It will be fully copied using its width
	 *            and height.
	 */
	public void setRect(int x, int y, FloatArray2D rect) {
		for (int dy = 0; dy < rect.height; dy++) {
			System.arraycopy(rect.data, dy * rect.width, data, x + (dy + y) * width, rect.width);
		}
	}

	public FloatArray2D getRect(int x, int y, int width, int height) {
		FloatArray2D rect = new FloatArray2D(width, height);
		for (int dy = 0; dy < height; dy++) {
			System.arraycopy(data, x + (dy + y) * this.width, rect.data, dy * width, width);
		}
		return rect;
	}

	public void setHLine(int y, float value) {
		Arrays.fill(data, y * width, (y + 1) * width, value);
	}

	public void setVLine(int x, float value) {
		for (int y = 0; y < height; y++) {
			set(x, y, value);
		}
	}
}
