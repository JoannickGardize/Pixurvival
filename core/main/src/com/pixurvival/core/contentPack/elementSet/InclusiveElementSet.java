package com.pixurvival.core.contentPack.elementSet;

import com.pixurvival.core.contentPack.IdentifiedElement;

public class InclusiveElementSet<T extends IdentifiedElement> extends CollectionElementSet<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean contains(T element) {
		return getElementSet().contains(element);
	}

	@Override
	public boolean containsById(int id) {
		return getIdSet().contains(id);
	}
}
