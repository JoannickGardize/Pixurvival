package com.pixurvival.contentPackEditor.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.pixurvival.core.contentPack.IdentifiedElement;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanUtils {

	@SneakyThrows
	public static <T> T newFilledInstance(Class<T> clazz) {
		T instance = clazz.newInstance();
		fill(instance);
		return instance;
	}

	public static <T> Supplier<T> filledSupplier(Class<T> clazz) {
		return () -> newFilledInstance(clazz);
	}

	public static void fill(Object instance) {
		Class<?> clazz = instance.getClass();
		forEachPropertyMethods(clazz, (getter, setter) -> {
			try {
				Object object = getter.invoke(instance);
				if (object == null) {
					Object attributeValue = newInstanceIfPossible(getter.getReturnType());
					if (attributeValue != null) {
						fill(attributeValue);
						setter.invoke(instance, attributeValue);
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
	}

	private static void forEachPropertyMethods(Class<?> clazz, BiConsumer<Method, Method> action) {
		for (Method getter : clazz.getMethods()) {
			if (getter.getName().startsWith("get") && getter.getName().length() > 3 && getter.getParameterCount() == 0 && !IdentifiedElement.class.isAssignableFrom(getter.getReturnType())) {
				try {
					Method setter = clazz.getMethod("set" + getter.getName().substring(3), getter.getReturnType());
					if (setter.getParameterCount() == 1 && setter.getParameters()[0].getType() == getter.getReturnType()) {
						action.accept(getter, setter);
					}
				} catch (NoSuchMethodException e) {
					// Nothing
				}
			}
		}
	}

	public static Object newInstanceIfPossible(Class<?> type) {
		if (type == String.class) {
			return null;
		} else if (Collection.class.isAssignableFrom(type)) {
			return new ArrayList<>();
		}
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
}
