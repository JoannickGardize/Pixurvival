package com.pixurvival.core.util;

import java.util.function.Supplier;

import lombok.Getter;

public class DoubleBufferedValue<T> {

	private @Getter T currentValue;
	private @Getter T previousValue;

	public DoubleBufferedValue(Supplier<T> initializer) {
		currentValue = initializer.get();
		previousValue = initializer.get();
	}

	public void swap() {
		T tmp = currentValue;
		currentValue = previousValue;
		previousValue = tmp;
	}
}
