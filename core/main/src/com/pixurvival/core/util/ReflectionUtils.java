package com.pixurvival.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtils {

	public static Method[] getAllMethods(Class<?> clazz) {
		List<Method> list = new ArrayList<>();
		Class<?> currentClass = clazz;
		while (currentClass != Object.class) {
			for (Method method : currentClass.getMethods()) {
				list.add(method);
			}
			currentClass = currentClass.getSuperclass();
		}
		return list.toArray(new Method[list.size()]);
	}

	public static Field[] getAllFields(Class<?> clazz) {
		List<Field> list = new ArrayList<>();
		Class<?> currentClass = clazz;
		while (currentClass != Object.class) {
			for (Field field : currentClass.getDeclaredFields()) {
				list.add(field);
			}
			currentClass = currentClass.getSuperclass();
		}
		return list.toArray(new Field[list.size()]);
	}

	public static Map<String, Field> getAllFieldsMap(Class<?> clazz) {
		Field[] fields = getAllFields(clazz);
		Map<String, Field> fieldMap = new HashMap<>();
		for (Field field : fields) {
			fieldMap.put(field.getName(), field);
		}
		return fieldMap;
	}

	public static void setAccessible(Field[] fields) {
		for (Field field : fields) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
		}
	}

	@SneakyThrows
	public static void setField(Field field, Object instance, Object value) {
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		field.set(instance, value);
	}

}
