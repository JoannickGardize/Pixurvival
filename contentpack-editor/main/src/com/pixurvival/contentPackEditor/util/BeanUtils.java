package com.pixurvival.contentPackEditor.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.pixurvival.core.util.ReflectionUtils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanUtils {

	@SneakyThrows
	public static <T> Function<T, Object> getGetter(Class<T> type, String attributeName) {
		Field field = ReflectionUtils.getField(type, attributeName);
		String preffix = field.getType() == boolean.class ? "is" : "get";
		Method getter = field.getDeclaringClass().getMethod(preffix + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1));
		return object -> {
			try {
				return getter.invoke(object);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new Error(e);
			}
		};
	}

	@SneakyThrows
	public static <T> BiConsumer<T, Object> getSetter(Class<T> type, String attributeName) {
		Field field = ReflectionUtils.getField(type, attributeName);
		Method setter = field.getDeclaringClass().getMethod("set" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1), field.getType());
		return (object, attribute) -> {
			try {
				setter.invoke(object, attribute);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new Error(e);
			}
		};
	}
}
