package com.pixurvival.core.reflection.visitor;

import java.util.Map;
import java.util.Map.Entry;

public class MapVisitor implements Visitor {

	@Override
	public void visit(VisitNode node, VisitHandler handler, VisitorContext context) {
		Map<?, ?> map = (Map<?, ?>) node.getObject();
		for (Entry<?, ?> entry : map.entrySet()) {
			VisitNode childNode = node.addChild(entry.getKey(), entry.getValue());
			context.getVisitorFor(childNode.getObject()).visit(childNode, handler, context);
		}
	}

}
