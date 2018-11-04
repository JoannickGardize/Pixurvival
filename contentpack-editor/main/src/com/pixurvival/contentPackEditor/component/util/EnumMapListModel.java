package com.pixurvival.contentPackEditor.component.util;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnumMapListModel<K extends Enum<K>, V> extends AbstractListModel<V> {

	private static final long serialVersionUID = 1L;

	@AllArgsConstructor
	private class Entry {
		K key;
		V value;
	}

	private List<Entry> list = new ArrayList<>();
	private @NonNull Class<K> enumType;

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public V getElementAt(int index) {
		return list.get(index).value;
	}

	public int put(K key, V value) {
		for (int i = 0; i < list.size(); i++) {
			int otherOrdinal = list.get(i).key.ordinal();
			if (otherOrdinal == key.ordinal()) {
				return -1;
			} else if (key.ordinal() < otherOrdinal) {
				list.add(i, new Entry(key, value));
				fireIntervalAdded(this, i, i);
				return i;
			}
		}
		int index = list.size();
		list.add(new Entry(key, value));
		fireIntervalAdded(this, index, index);
		return index;
	}

	public void remove(int index) {
		list.remove(index);
		fireIntervalRemoved(this, index, index);
	}

	public void setMap(Map<K, V> map) {
		if (!list.isEmpty()) {
			fireIntervalRemoved(this, 0, list.size() - 1);
		}
		list.clear();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			list.add(new Entry(entry.getKey(), entry.getValue()));
		}
		if (!list.isEmpty()) {
			fireIntervalAdded(this, 0, list.size() - 1);
		}
	}

	public boolean contains(K element) {
		for (Entry entry : list) {
			if (entry.key == element) {
				return true;
			}
		}
		return false;
	}

	public List<V> values() {
		List<V> values = new ArrayList<>(list.size());
		for (Entry entry : list) {
			values.add(entry.value);
		}
		return values;
	}

	public Map<K, V> toMap() {
		Map<K, V> map = new EnumMap<>(enumType);
		for (Entry entry : list) {
			map.put(entry.key, entry.value);
		}
		return map;
	}

}
