package com.pixurvival.contentPackEditor.util;

import java.lang.reflect.Method;

import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanUtils {

	@SneakyThrows
	public static <T> T newFilledInstance(Class<T> clazz) {
		T instance = clazz.newInstance();
		for (Method method : clazz.getMethods()) {
			if (method.getName().startsWith("get") && method.getParameterCount() == 0 && !IdentifiedElement.class.isAssignableFrom(method.getReturnType())) {
				Object object = method.invoke(instance);
				if (object == null) {
					Object attributeValue = newInstanceIfPossible(method.getReturnType());
					try {
						Method setter = clazz.getMethod("set" + method.getName().substring(3), method.getReturnType());
						if (setter.getParameterCount() == 1 && setter.getParameters()[0].getType() == method.getReturnType()) {
							setter.invoke(instance, attributeValue);
						}
					} catch (NoSuchMethodException e) {
						// Nothing
					}
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
