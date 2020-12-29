package com.pixurvival.core.reflection.visitor;

import java.util.Collection;

public class CollectionVisitor implements Visitor {

	@Override
	public void visit(VisitNode node, VisitHandler handler, VisitorContext context) {
		Collection<?> list = (Collection<?>) node.getObject();
		int i = 0;
		for (Object o : list) {
			VisitNode childNode = node.addChild(i++, o);
			handler.visit(childNode);
			if (context.getTraversalCondition().test(childNode)) {
				context.getVisitorFor(childNode.getObject()).visit(childNode, handler, context);
			}
		}
	}
}
