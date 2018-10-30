package com.pixurvival.contentPackEditor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedElement;
import com.pixurvival.core.contentPack.ResourceReference;
import com.pixurvival.core.contentPack.Subelement;
import com.pixurvival.core.util.CaseUtils;

public class ElementEditionEntry {

	private static class FieldPath {
		private List<Field> fields = new ArrayList<>();

		public FieldPath() {
			fields = new ArrayList<>();
		}

		public FieldPath(FieldPath other) {
			fields = new ArrayList<>(other.fields);
		}

		public FieldPath add(Field field) {
			field.setAccessible(true);
			fields.add(field);
			return this;
		}

		public Object get(Object object) throws IllegalArgumentException, IllegalAccessException {
			Object currentObject = object;
			for (Field field : fields) {
				currentObject = field.get(object);
			}
			return currentObject;

		}

		public void set(Object object, Object value) throws IllegalArgumentException, IllegalAccessException {
			Object currentObject = object;
			for (int i = 0; i < fields.size() - 1; i++) {
				currentObject = fields.get(i).get(currentObject);
			}
			fields.get(fields.size() - 1).set(currentObject, value);
		}

	}

	private List<FieldPath> resourceRefs = new ArrayList<>();
	private Method listGetter;

	public ElementEditionEntry(ElementType elementType) {
		registerClass(new FieldPath(), elementType.getElementClass());
		String methodName = CaseUtils.upperToCamelCase("GET_" + elementType.name()) + "s";
		try {
			listGetter = ContentPack.class.getMethod(methodName);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public boolean hasResourceRef(NamedElement element, String resourceRef) {
		try {
			for (FieldPath fieldPath : resourceRefs) {
				if (resourceRef.equals(fieldPath.get(element))) {
					return true;
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void removeResourceRef(NamedElement element, String resourceRef) {
		try {
			for (FieldPath fieldPath : resourceRefs) {
				if (resourceRef.equals(fieldPath.get(element))) {
					fieldPath.set(element, null);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public boolean isValid(NamedElement element) {
		try {
			for (FieldPath fieldPath : resourceRefs) {
				if (!ResourcesService.getInstance().containsResource((String) fieldPath.get(element))) {
					return false;
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<NamedElement> getList() {
		ContentPack contentPack = FileService.getInstance().getCurrentContentPack();
		try {
			return (List<NamedElement>) listGetter.invoke(contentPack);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void registerClass(FieldPath fieldPath, Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(ResourceReference.class)) {
				resourceRefs.add(new FieldPath(fieldPath).add(field));
			} else if (field.isAnnotationPresent(ResourceReference.class)) {
			} else if (field.isAnnotationPresent(Subelement.class)) {
				registerClass(new FieldPath(fieldPath).add(field), field.getType());
			}
		}
	}
}
