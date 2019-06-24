package com.pixurvival.core.contentPack.serialization;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import com.pixurvival.core.util.ReflectionUtils;

/**
 * Custom property order implementation for Yaml serialization, replacing the
 * default alphabetical order by the order the field are declared in the class,
 * with the super class fields before, if any.
 * 
 * @author SharkHendrix
 *
 */
public class CustomPropertyOrderUtils extends PropertyUtils {

	private static class OrderEntry {
		private Map<String, Integer> orderMap = new HashMap<>();

		public OrderEntry(Class<?> clazz) {
			Field[] fields = ReflectionUtils.getAllFields(clazz);
			for (int i = 0; i < fields.length; i++) {
				orderMap.put(fields[i].getName(), i);
			}
		}

		public int getOrder(String fieldName) {
			return orderMap.get(fieldName);
		}
	}

	private Map<Class<?>, OrderEntry> orderEntries = new HashMap<>();

	@Override
	protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
		Set<Property> properties = super.createPropertySet(type, bAccess);
		OrderEntry orderEntry = orderEntries.computeIfAbsent(type, OrderEntry::new);
		Set<Property> orderedResult = new TreeSet<>((o1, o2) -> orderEntry.getOrder(o1.getName()) - orderEntry.getOrder(o2.getName()));
		orderedResult.addAll(properties);
		return orderedResult;
	}
}
