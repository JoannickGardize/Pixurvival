package com.pixurvival.core.contentPack.validation.handler;

import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.Pattern;
import com.pixurvival.core.reflection.visitor.VisitNode;
import com.pixurvival.core.util.Cache;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

public class PatternHandler implements AnnotationHandler {

    private Cache<String, java.util.regex.Pattern> patternChache = new Cache<>(java.util.regex.Pattern::compile);

    @Override
    public Collection<Class<? extends Annotation>> getHandledAnnotations() {
        return Collections.singleton(Pattern.class);
    }

    @Override
    public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
        Pattern pattern = (Pattern) annotation;
        if (!patternChache.get(pattern.value()).matcher(node.getObject().toString()).matches()) {
            errors.add(node, pattern);
        }
    }
}
