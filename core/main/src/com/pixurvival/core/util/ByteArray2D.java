package com.pixurvival.core.util;

import java.util.Arrays;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class ByteArray2D {
	@Getter
	private int width;
	@Getter
	private int height;
	private byte[] data;

	public ByteArray2D(int width, int height) {
		this.width = width;
		this.height = height;

		data = new byte[width * height];
	}

	public byte get(int x, int y) {
		return data[x + y * width];
	}

	public void set(int x, int y, byte element) {
		data[x + y * width] = element;
	}

	public void fill(byte element) {
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
	public void setRect(int x, int y, ByteArray2D rect) {
		for (int dy = 0; dy < rect.height; dy++) {
			System.arraycopy(rect.data, dy * rect.width, data, x + (dy + y) * width, rect.width);
		}
	}

	public ByteArray2D getRect(int x, int y, int width, int height) {
		ByteArray2D rect = new ByteArray2D(width, height);
		for (int dy = 0; dy < height; dy++) {
			System.arraycopy(data, x + (dy + y) * this.width, rect.data, dy * width, width);
		}
		return rect;
	}
}
