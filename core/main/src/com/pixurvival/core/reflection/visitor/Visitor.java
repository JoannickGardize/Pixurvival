package com.pixurvival.core.reflection.visitor;

@FunctionalInterface
public interface Visitor {

    void visit(VisitNode node, VisitHandler handler, VisitorContext context);
}
