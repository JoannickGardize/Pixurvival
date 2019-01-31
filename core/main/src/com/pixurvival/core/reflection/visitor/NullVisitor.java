package com.pixurvival.core.reflection.visitor;

public class NullVisitor implements Visitor {

	@Override
	public void visit(VisitNode node, VisitHandler handler, VisitorContext context) {
		handler.visit(node);
	}

}
