package com.pixurvival.core.reflection.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import com.pixurvival.core.util.ReflectionUtils;

import lombok.SneakyThrows;

public class FieldsVisitor implements Visitor {

	private static final int MODIFIER_FILTER = Modifier.TRANSIENT | Modifier.STATIC;

	private final Field[] fields;

	public FieldsVisitor(Class<?> clazz) {
		fields = Arrays.stream(ReflectionUtils.getAllFields(clazz)).filter(f -> (f.getModifiers() & MODIFIER_FILTER) == 0).toArray(Field[]::new);
		ReflectionUtils.setAccessible(fields);
	}

	@Override
	@SneakyThrows
	public void visit(VisitNode node, VisitHandler handler, VisitorContext context) {
		if (node.getObject() == null) {
			return;
		}
		for (Field field : fields) {
			VisitNode childNode = node.addChild(field, field.get(node.getObject()));
			handler.visit(childNode);
			if (context.getTraversalCondition().test(childNode)) {
				context.getVisitorFor(childNode.getObject()).visit(childNode, handler, context);
			}
		}
	}
}
