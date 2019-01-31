package com.pixurvival.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReflectionUtil {

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

	public void setAccessible(Field[] fields) {
		for (Field field : fields) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
		}
	}
}
