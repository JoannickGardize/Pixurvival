package com.pixurvival.contentPackEditor.component.factory;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import lombok.Getter;
import lombok.SneakyThrows;

public class AttributeEditorFactory {

	public static final @Getter AttributeEditorFactory instance = new AttributeEditorFactory();

	private Map<Class<?>, TypeEditorFactory> factory = new HashMap<>();

	private AttributeEditorFactory() {
	}

	@SneakyThrows
	public <T> JComponent build(Class<T> type, String attributeName, AttributeEditorFlag... flags) {
		Field field = type.getDeclaredField(attributeName);

		return factory.get(field.getType()).build(field, flags.length > 0 ? EnumSet.of(flags[0], flags) : EnumSet.noneOf(AttributeEditorFlag.class));
	}

}
