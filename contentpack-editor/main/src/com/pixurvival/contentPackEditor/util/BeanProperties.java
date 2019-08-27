package com.pixurvival.contentPackEditor.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.util.ReflectionUtils;

public class BeanProperties {

	private Map<String, BeanProperty> propertyMap = new HashMap<>();

	public BeanProperties(Class<?> type) {
		for (Field field : ReflectionUtils.getAllFields(type)) {
			BeanProperty beanProperty = BeanProperty.createIfPossible(type, field);
			if (beanProperty != null) {
				propertyMap.put(beanProperty.getName(), beanProperty);
			}
		}
	}

	public Collection<BeanProperty> getProperties() {
		return propertyMap.values();
	}

	public BeanProperty getProperty(String name) {
		return propertyMap.get(name);
	}
}
