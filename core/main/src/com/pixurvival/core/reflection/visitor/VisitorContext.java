package com.pixurvival.core.reflection.visitor;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class VisitorContext {

    private static final Visitor COLLECTION_VISITOR = new CollectionVisitor();
    private static final Visitor MAP_VISITOR = new MapVisitor();
    private static final Visitor NULL_VISITOR = (n, h, c) -> {
    };

    private Map<Class<?>, FieldsVisitor> fieldsVisitors = new HashMap<>();

    private @Getter
    @Setter Predicate<VisitNode> traversalCondition = f -> true;

    public Visitor getVisitorFor(Object object) {
        if (object == null) {
            return NULL_VISITOR;
        } else if (object instanceof Collection) {
            return COLLECTION_VISITOR;
        } else if (object instanceof Map) {
            return MAP_VISITOR;
        } else {
            return fieldsVisitors.computeIfAbsent(object.getClass(), FieldsVisitor::new);
        }
    }

    public VisitNode visit(Object object, VisitHandler handler) {
        VisitNode rootNode = new VisitNode(object);
        getVisitorFor(object).visit(rootNode, handler, this);
        return rootNode;
    }
}
