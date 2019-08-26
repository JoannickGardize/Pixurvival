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

	public static boolean containsIdentity(Collection<?> collection, Object o) {
		for (Object element : collection) {
			if (o == element) {
				return true;
			}
		}
		return false;
	}

	public static String toString(Collection<?> collection) {
		if (collection.size() == 1) {
			return collection.iterator().next().toString();
		}
		StringBuilder sb = new StringBuilder("[");
		String separator = "";
		for (Object o : collection) {
			sb.append(separator).append(o);
			separator = ", ";
		}
		sb.append("]");
		return sb.toString();
	}

	public static <T> T get(Collection<T> collection, int index) {
		if (collection instanceof List) {
			return ((List<T>) collection).get(index);
		} else {
			int i = 0;
			for (T e : collection) {
				if (i == index) {
					return e;
				}
				i++;
			}
			throw new IndexOutOfBoundsException();
		}
	}
}
