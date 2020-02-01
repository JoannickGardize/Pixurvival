package com.pixurvival.gdxcore.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Enums {

	private static Map<Class<? extends Enum<?>>, Map<String, ? extends Enum<?>>> nameMaps = new WeakHashMap<>();

	@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> T valueOfOrNull(Class<T> type, String name) {
		return (T) nameMaps.computeIfAbsent(type, t -> {
			Map<String, T> nameMap = new HashMap<>();
			T[] values;
			try {
				values = (T[]) type.getMethod("values").invoke(null);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
			for (T value : values) {
				nameMap.put(value.name(), value);
			}
			return nameMap;
		}).get(name);
	}
}
