package com.pixurvival.contentPackEditor.component.factory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.swing.JComponent;

import lombok.Getter;

public class AttributeEditorFactory {

	public static final @Getter AttributeEditorFactory instance = new AttributeEditorFactory();

	private Map<AttributeEditorKey, Function<Field, JComponent>> factory = new HashMap<>();

	private AttributeEditorFactory() {
	}

	public JComponent build(Class<?> type, String fieldName) {
		return null;
	}

}
