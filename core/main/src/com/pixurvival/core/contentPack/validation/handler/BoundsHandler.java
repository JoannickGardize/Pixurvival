package com.pixurvival.core.contentPack.validation.handler;

import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.reflection.visitor.VisitNode;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

public class BoundsHandler implements AnnotationHandler {

    @Override
    public Collection<Class<? extends Annotation>> getHandledAnnotations() {
        return Collections.singleton(Bounds.class);
    }

    @Override
    public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
        Bounds bounds = (Bounds) annotation;
        float value = ((Number) node.getObject()).floatValue();
        if (value < bounds.min() || value > bounds.max() || !bounds.minInclusive() && value == bounds.min() || !bounds.maxInclusive() && value == bounds.max()) {
            errors.add(node, bounds);
        }
    }
}
