package com.pixurvival.contentPackEditor.util;

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

}
