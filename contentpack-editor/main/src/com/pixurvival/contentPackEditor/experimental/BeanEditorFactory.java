package com.pixurvival.contentPackEditor.experimental;

import java.util.Map;
import java.util.function.Supplier;

import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;

public class BeanEditorFactory {

	private Map<Class<?>, Supplier<ValueComponent<?>>> componentsMap;

	public <T> void register(Class<T> type, Supplier<ValueComponent<T>> componentSupplier) {

	}

	public void register(Class<?> type, ElementLayout layout) {

	}

	public <T> ValueComponent<T> createComponentFor(Class<T> type) {
		return null;
	}
}
