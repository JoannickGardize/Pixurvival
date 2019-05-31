package com.pixurvival.contentPackEditor.util;

import java.lang.reflect.Method;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanUtils {

	@SneakyThrows
	public static <T> T newFilledInstance(Class<T> clazz) {
		T instance = clazz.newInstance();
		for (Method method : clazz.getMethods()) {
			if (method.getName().startsWith("get")) {
				Object object = method.invoke(instance);
				if (object == null) {
					Object attributeValue = newInstanceIfPossible(method.getReturnType());
					clazz.getMethod("set" + method.getName().substring(3), method.getReturnType()).invoke(instance, attributeValue);
				}
			}
		}
		return instance;
	}

	public static Object newInstanceIfPossible(Class<?> type) {
		if (type == String.class) {
			return null;
		}
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
}
