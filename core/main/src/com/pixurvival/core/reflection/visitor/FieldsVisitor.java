package com.pixurvival.core.reflection.visitor;

import java.lang.reflect.Field;
import java.util.Arrays;

import com.pixurvival.core.util.ReflectionUtils;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

public class FieldsVisitor implements Visitor {

	@AllArgsConstructor
	public static class FieldNode {
		Field field;
		boolean traverse;
	}

	private final FieldNode[] fieldNodes;

	public FieldsVisitor(VisitorContext context, Class<?> clazz) {
		Field[] fields = ReflectionUtils.getAllFields(clazz);
		ReflectionUtils.setAccessible(fields);
		fieldNodes = Arrays.stream(fields)
				.map(field -> new FieldNode(field, context.getTraversalCondition().test(field)))
				.toArray(FieldNode[]::new);
	}

	@Override
	@SneakyThrows
	public void visit(VisitNode node, VisitHandler handler, VisitorContext context) {
		if (node.getObject() == null) {
			return;
		}
		for (FieldNode fieldNode : fieldNodes) {
			VisitNode childNode = node.addChild(fieldNode.field, fieldNode.field.get(node.getObject()));
			if (handler.visit(childNode) && fieldNode.traverse && childNode.getObject() != null) {
				context.getVisitorFor(childNode.getObject()).visit(childNode, handler, context);
			}
		}
	}
}
