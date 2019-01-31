package com.pixurvival.contentPackEditor.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashBiMap<K, V> implements BiMap<K, V> {

	private Map<K, V> map = new HashMap<>();
	private HashBiMap<V, K> inverse = new HashBiMap<>();

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public V put(K key, V value) {
		inverse.map.put(value, key);
		return map.put(key, value);
	}

	@Override
	public V remove(Object key) {
		V value = map.remove(key);
		inverse.map.remove(value);
		return value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		map.clear();
		inverse.map.clear();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	@Override
	public BiMap<V, K> inverse() {
		return inverse;
	}

}
