package com.pixurvival.core.contentPack.validation.handler;

import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.ImageAccessor;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.reflection.visitor.VisitNode;
import com.pixurvival.core.util.FileUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

public class ResourceReferenceHandler implements AnnotationHandler {

    private ImageAccessor imageAccessor;

    @Override
    public void begin(ImageAccessor imageAccessor) {
        this.imageAccessor = imageAccessor;
    }

    @Override
    public Collection<Class<? extends Annotation>> getHandledAnnotations() {
        return Collections.singleton(ResourceReference.class);
    }

    @Override
    public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {

        ResourceReference resourceReference = (ResourceReference) annotation;
        switch (resourceReference.type()) {
            case IMAGE:
                if (imageAccessor.get((String) node.getObject()) == null) {
                    errors.add(node, annotation);
                }
                break;
            case SOUND:
                String extension = FileUtils.fileExtensionOf((String) node.getObject());
                if (!extension.equals("wav") && !extension.equals("mp3")) {
                    errors.add(node, annotation);
                }
                break;
            default:
                break;

        }
    }

}
