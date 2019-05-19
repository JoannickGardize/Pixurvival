package com.pixurvival.core.util;

import java.util.Collection;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtils {

	public static <E> boolean addIfNotPresent(Collection<E> collection, E element) {
		if (!collection.contains(element)) {
			return collection.add(element);
		}
		return false;
	}

	public static <E> boolean addOrReplace(List<E> collection, E element) {
		int index = collection.indexOf(element);
		if (index == -1) {
			collection.add(element);
		} else {
			collection.set(index, element);
		}
		return true;
	}

	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
}
