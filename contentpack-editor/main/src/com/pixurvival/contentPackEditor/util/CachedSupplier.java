package com.pixurvival.contentPackEditor.util;

import java.util.function.Supplier;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachedSupplier<T> {
	private @NonNull Supplier<T> supplier;
	private T value;

	public T get() {
		if (value == null) {
			value = supplier.get();
		}
		return value;
	}

	public T getNew() {
		value = supplier.get();
		return value;
	}
}
