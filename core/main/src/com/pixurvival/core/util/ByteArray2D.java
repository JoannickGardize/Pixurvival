package com.pixurvival.core.util;

import java.util.Arrays;

import lombok.Getter;

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

	public void setAll(byte element) {
		Arrays.fill(data, element);
	}

	/**
	 * Fill this Array 2D with the given Array 2D. This method is super efficient,
	 * using {@link System#arraycopy(Object, int, Object, int, int)} for each rows.
	 * 
	 * @param x
	 *            The start X position where the rectangle will be copied.
	 * @param y
	 *            The start Y position where the rectangle will be copied.
	 * @param rect
	 *            The rectangle to copy. It will be fully copied using its width and
	 *            height.
	 */
	public void setRect(int x, int y, ByteArray2D rect) {
		int endY = y + rect.height;
		for (int dy = y; dy < endY; dy++) {
			System.arraycopy(rect.data, 0, data, x + dy * width, rect.width);
		}
	}

	public ByteArray2D getRect(int x, int y, int width, int height) {
		ByteArray2D rect = new ByteArray2D(width, height);
		int endY = y + rect.height;
		for (int dy = y; dy < endY; dy++) {
			System.arraycopy(data, x + dy * this.width, rect.data, dy * rect.width, rect.width);
		}
		return rect;
	}
}
