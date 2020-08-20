package com.pixurvival.core.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.pixurvival.core.contentPack.IdentifiedElement;

public class IdSetHelper {

	private Set<Integer> idSet;

	public Set<Integer> get(Collection<? extends IdentifiedElement> collection) {
		if (idSet == null) {
			idSet = new HashSet<>(collection.size(), 1);
			for (IdentifiedElement element : collection) {
				idSet.add(element.getId());
			}
		}
		return idSet;
	}
}
