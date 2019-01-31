package com.pixurvival.core.util;

import java.util.Random;

public class IntUrn {

	private int currentSize;
	private int[] content;

	public IntUrn(int size) {
		currentSize = size;
		content = new int[size];
		for (int i = 0; i < size; i++) {
			content[i] = i;
		}
	}

	public boolean isEmpty() {
		return currentSize <= 0;
	}

	public int draw(Random random) {
		int index = random.nextInt(currentSize);
		currentSize--;
		int result = content[index];
		content[index] = content[currentSize];
		return result;

	}
}
