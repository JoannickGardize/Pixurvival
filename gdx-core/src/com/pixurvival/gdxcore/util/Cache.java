package com.pixurvival.gdxcore.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Cache<K, V> {

	private Map<K, SoftReference<V>> map = new HashMap<>();
	private @NonNull Function<K, V> producer;

	public V get(K key) {
		SoftReference<V> ref = map.get(key);
		V result;
		if (ref == null || (result = ref.get()) == null) {
			result = producer.apply(key);
			map.put(key, new SoftReference<>(result));
		}
		return result;
	}
}
