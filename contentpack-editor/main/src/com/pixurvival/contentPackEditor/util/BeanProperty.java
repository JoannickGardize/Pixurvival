package com.pixurvival.contentPackEditor.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.pixurvival.core.util.CaseUtils;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

/**
 * Represents a Bean property according to the standard definition of Java Bean.
 * the property must have a getter, a setter, and not being transient.
 * 
 * @author SharkHendrix
 *
 */
@AllArgsConstructor
public class BeanProperty {

	private Field field;
	private Method getter;
	private Method setter;

	public static BeanProperty createIfPossible(Class<?> type, Field field) {
		if (Modifier.isTransient(field.getModifiers())) {
			return null;
		}
		try {
			String preffix = field.getType() == boolean.class ? "is" : "get";
			String pascalName = CaseUtils.camelToPascalCase(field.getName());
			Method getter = type.getMethod(preffix + pascalName);
			if (getter.getReturnType() != field.getType()) {
				return null;
			}
			Method setter = type.getMethod("set" + pascalName);
			if (setter.getReturnType() != Void.TYPE) {
				return null;
			}
			return new BeanProperty(field, getter, setter);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	public String getName() {
		return field.getName();
	}

	public Class<?> getType() {
		return field.getType();
	}

	@SneakyThrows
	public Object get(Object instance) {
		return getter.invoke(instance);
	}

	@SneakyThrows
	public void set(Object instance, Object value) {
		setter.invoke(instance, value);
	}
}
