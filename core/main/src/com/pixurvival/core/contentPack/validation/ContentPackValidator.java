package com.pixurvival.core.contentPack.validation;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementReferenceOrValid;
import com.pixurvival.core.contentPack.validation.annotation.Nullable;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.contentPack.validation.handler.*;
import com.pixurvival.core.reflection.visitor.VisitNode;
import com.pixurvival.core.reflection.visitor.VisitorContext;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ContentPackValidator {

    private Collection<AnnotationHandler> annotationHandlers = new ArrayList<>();
    private Map<Class<? extends Annotation>, AnnotationHandler> annotationHandlersPerAnnotations = new HashMap<>();

    private VisitorContext context = new VisitorContext();

    @Getter
    @Setter
    private ImageAccessor imageAccessor = new DefaultImageAccessor();

    public ContentPackValidator() {
        context.setTraversalCondition(node -> {
            Field field;
            if (node.getKey() instanceof Field) {
                field = (Field) node.getKey();
            } else {
                field = (Field) node.getParent().getKey();
            }
            return field.isAnnotationPresent(Valid.class) || field.isAnnotationPresent(ElementReferenceOrValid.class) && !(node.getObject() instanceof NamedIdentifiedElement);
        });
        addAnnotationHandler(new BoundsHandler());
        addAnnotationHandler(new ElementListHandler());
        addAnnotationHandler(new ElementReferenceHandler());
        addAnnotationHandler(new LengthHandler());
        addAnnotationHandler(new PatternHandler());
        addAnnotationHandler(new PositiveHandler());
        addAnnotationHandler(new ResourceReferenceHandler());
        addAnnotationHandler(new SpriteWidthHandler());
        addAnnotationHandler(new SpriteHeightHandler());
        addAnnotationHandler(new AnimationTemplateRequirementHandler());
        addAnnotationHandler(new RequiredEquipmentOffsetHandler());
        addAnnotationHandler(new AnimationTemplateFramesHandler());
        addAnnotationHandler(new EquipmentOffsetFramesHandler());
        addAnnotationHandler(new AbilityIndexesHandler());
        addAnnotationHandler(new UnitSpriteSheetHandler());
        addAnnotationHandler(new UnitSpriteFrameHandler());
        addAnnotationHandler(new AscendingHandler());
        addAnnotationHandler(new StaticMapResourcesHandler());
        addAnnotationHandler(new UniqueHandler());
    }

    public ErrorCollection validate(ContentPack contentPack) {
        ErrorCollection errors = new ErrorCollection();
        contentPack.initialize();
        imageAccessor.begin(contentPack);
        annotationHandlers.forEach(h -> h.begin(imageAccessor));
        context.visit(contentPack, n -> validate(n, errors));
        annotationHandlers.forEach(h -> h.end(errors));
        imageAccessor.end();
        return errors;
    }

    private void validate(VisitNode node, ErrorCollection errors) {
        if (node.getKey() instanceof Field) {
            Field field = (Field) node.getKey();
            if (node.getObject() != null) {
                for (Annotation annotation : field.getAnnotations()) {
                    handleAnnotation(node, errors, annotation);
                }
            } else {
                handleNullable(node, errors);
            }
        } else if (node.getObject() != null) {
            for (Annotation annotation : node.getObject().getClass().getAnnotations()) {
                handleAnnotation(node, errors, annotation);
            }
        }
    }

    private void handleAnnotation(VisitNode node, ErrorCollection errors, Annotation annotation) {
        AnnotationHandler handler = annotationHandlersPerAnnotations.get(annotation.annotationType());
        if (handler != null) {
            handler.handle(node, annotation, errors);
        }
    }

    private void handleNullable(VisitNode node, ErrorCollection errors) {
        if (node.getObject() == null && !((Field) node.getKey()).isAnnotationPresent(Nullable.class)) {
            errors.add(node, new NullErrorCause());
        }
    }

    private void addAnnotationHandler(AnnotationHandler handler) {
        annotationHandlers.add(handler);
        for (Class<? extends Annotation> annotationType : handler.getHandledAnnotations()) {
            annotationHandlersPerAnnotations.put(annotationType, handler);
        }
    }
}
