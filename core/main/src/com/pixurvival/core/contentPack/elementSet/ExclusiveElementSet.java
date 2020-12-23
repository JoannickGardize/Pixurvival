package com.pixurvival.core.contentPack.elementSet;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;

public class ExclusiveElementSet<T extends NamedIdentifiedElement> extends CollectionElementSet<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean contains(T element) {
		return !getElementSet().contains(element);
	}

	@Override
	public boolean containsById(int id) {
		return !getIdSet().contains(id);
	}
}
