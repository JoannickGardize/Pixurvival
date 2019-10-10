package com.pixurvival.core.util;

import java.util.Collection;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtils {

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
