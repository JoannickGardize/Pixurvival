package com.pixurvival.core.reflection.visitor;

public interface Visitor {

	default void visit(Object object, VisitHandler handler, VisitorContext context) {
		visit(new VisitNode(object), handler, context);
	}

	void visit(VisitNode node, VisitHandler handler, VisitorContext context);

}
