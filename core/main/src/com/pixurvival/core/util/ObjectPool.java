package com.pixurvival.core.util;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ObjectPool<T extends Poolable> {

	private Supplier<T> instanceSupplier;

	private List<Reference<T>> pool = new ArrayList<>();

	public ObjectPool(Supplier<T> instanceSupplier) {
		this.instanceSupplier = instanceSupplier;
	}

	public synchronized T get() {
		T object = null;
		while (!pool.isEmpty() && (object = pool.remove(pool.size() - 1).get()) == null)
			;
		if (object == null) {
			return instanceSupplier.get();
		}
		return object;
	}

	public synchronized void offer(T object) {
		object.clear();
		pool.add(new SoftReference<>(object));
	}
}
