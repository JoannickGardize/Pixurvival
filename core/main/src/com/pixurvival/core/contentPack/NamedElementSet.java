package com.pixurvival.core.contentPack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.util.ListViewOfMap;

public class NamedElementSet<T extends NamedElement> {

	private Map<String, T> elements = new HashMap<>();

	public Map<String, T> all() {
		return Collections.unmodifiableMap(elements);
	}

	public T get(String name) {
		return elements.get(name);
	}

	public List<T> getListView() {
		return new ListViewOfMap<String, T>(elements, NamedElement::getName);
	}

	public void finalizeElements() {

	}
}
