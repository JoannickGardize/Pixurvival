package com.pixurvival.core.reflection.visitor;

@FunctionalInterface
public interface VisitHandler {

    void visit(VisitNode node);
}
