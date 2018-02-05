package fr.sharkhendrix.pixurvival.core.util;

import java.util.Arrays;

import lombok.Getter;

public class Array2D<E> {

	@Getter
	private int width;
	@Getter
	private int height;
	private Object[] data;

	public Array2D(int width, int height) {
		this.width = width;
		this.height = height;

		data = new Object[width * height];
	}

	@SuppressWarnings("unchecked")
	public E get(int x, int y) {
		return (E) data[x + y * width];
	}

	public void set(int x, int y, E element) {
		data[x + y * width] = element;
	}

	public void setAll(E element) {
		Arrays.fill(data, element);
	}
}
