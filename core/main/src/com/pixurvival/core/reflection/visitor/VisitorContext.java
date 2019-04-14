package com.pixurvival.core.reflection.visitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.NONE)
public class VisitorContext {

	private static @Getter VisitorContext instance = new VisitorContext();

	private final Visitor listVisitor = new ListVisitor();
	private final Visitor mapVisitor = new MapVisitor();
	private final Visitor nullVisitor = new NullVisitor();

	private Map<Class<?>, Visitor> visitors = new HashMap<>();

	private @Getter @Setter Predicate<Field> traversalCondition = f -> true;

	public Visitor getVisitorFor(Object object) {
		if (object == null) {
			return nullVisitor;
		}
		Class<?> clazz = object.getClass();
		if (List.class.isAssignableFrom(clazz)) {
			return listVisitor;
		} else if (Map.class.isAssignableFrom(clazz)) {
			return mapVisitor;
		} else {
			return visitors.computeIfAbsent(clazz, c -> new FieldsVisitor(this, c));
		}
	}

	public VisitNode visit(Object object, VisitHandler handler) {
		VisitNode rootNode = new VisitNode(object);
		getVisitorFor(object).visit(rootNode, handler, this);
		return rootNode;
	}

	public void setTraversalAnnotation(Class<? extends Annotation> annotationClass) {
		traversalCondition = f -> f.isAnnotationPresent(annotationClass);
	}

}
