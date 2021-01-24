package com.pixurvival.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
			list.addAll(Arrays.asList(currentClass.getMethods()));
			currentClass = currentClass.getSuperclass();
		}
		return list.toArray(new Method[list.size()]);
	}

	/**
	 * @param clazz
	 * @return All the field of the class and superclasses, with superclasses fields
	 *         first.
	 */
	public static Field[] getAllFields(Class<?> clazz) {
		List<Field> list = new ArrayList<>();
		Class<?> currentClass = clazz;
		List<Class<?>> classHierarchy = new ArrayList<>();
		while (currentClass != Object.class) {
			classHierarchy.add(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		for (int i = classHierarchy.size() - 1; i >= 0; i--) {
			for (Field field : classHierarchy.get(i).getDeclaredFields()) {
				list.add(field);
			}
		}
		return list.toArray(new Field[list.size()]);
	}

	public static Field[] getAnnotedFields(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Field> list = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(annotation)) {
				list.add(field);
			}
		}
		return list.toArray(new Field[list.size()]);

	}

	@SneakyThrows
	public static Object getByGetter(Object object, Field field) {
		return object.getClass().getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1)).invoke(object);
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

	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> getSuperClassUnder(Class<? extends T> type, Class<T> under) {
		Class<? extends T> tmp = type;
		while (tmp.getSuperclass() != under) {
			tmp = (Class<? extends T>) tmp.getSuperclass();
		}
		return tmp;
	}

	/**
	 * Get the field from the given class with the given name, goes up to superclass
	 * until it find a field with the given name.
	 * 
	 * @param type
	 * @param name
	 * @return
	 * @throws NoSuchFieldException
	 */
	public static Field getField(Class<?> type, String name) throws NoSuchFieldException {
		Class<?> currentType = type;
		while (currentType != Object.class) {
			try {
				return currentType.getDeclaredField(name);
			} catch (NoSuchFieldException | SecurityException e) {
				// Silently ignore
			}
			currentType = currentType.getSuperclass();
		}
		throw new NoSuchFieldException("Field " + name + " does not exists in type " + type + " or its superclasses");
	}

	public static Class<?> getGenericTypeArgument(Field field) {
		ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
		Type type = parameterizedType.getActualTypeArguments()[0];
		if (type instanceof Class) {
			return (Class<?>) parameterizedType.getActualTypeArguments()[0];
		} else {
			return null;
		}
	}
}
