package com.pixurvival.core.contentPack.elementSet;

import com.pixurvival.core.contentPack.IdentifiedElement;

public class AllElementSet<T extends IdentifiedElement> implements ElementSet<T> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean contains(T element) {
		return true;
	}

	@Override
	public boolean containsById(int id) {
		return true;
	}

}
