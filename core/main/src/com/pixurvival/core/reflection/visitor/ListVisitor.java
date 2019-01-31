package com.pixurvival.core.reflection.visitor;

import java.util.List;

public class ListVisitor implements Visitor {

	@Override
	public void visit(VisitNode node, VisitHandler handler, VisitorContext context) {
		List<?> list = (List<?>) node.getObject();
		for (int i = 0; i < list.size(); i++) {
			VisitNode childNode = node.addChild(i, list.get(i));
			context.getVisitorFor(childNode.getObject()).visit(childNode, handler, context);
		}
	}
}
