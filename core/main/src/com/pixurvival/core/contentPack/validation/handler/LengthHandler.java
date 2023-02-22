package com.pixurvival.core.contentPack.validation.handler;

import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.reflection.visitor.VisitNode;
import com.pixurvival.core.util.Sized;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class LengthHandler implements AnnotationHandler {

    @Override
    public Collection<Class<? extends Annotation>> getHandledAnnotations() {
        return Collections.singleton(Length.class);
    }

    @Override
    public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
        Length length = (Length) annotation;
        Object object = node.getObject();
        int actualLength = 0;
        if (object instanceof String) {
            actualLength = ((String) object).length();
        } else if (object instanceof Collection) {
            actualLength = ((Collection<?>) object).size();
        } else if (object instanceof Map) {
            actualLength = ((Map<?, ?>) object).size();
        } else if (object instanceof Sized) {
            actualLength = ((Sized) object).size();
        } else {
            throw new IllegalArgumentException("Unsupported type for annotation Length: " + object.getClass());
        }
        if (actualLength < length.min() || actualLength >= length.max()) {
            errors.add(node, length);
        }
    }
}
