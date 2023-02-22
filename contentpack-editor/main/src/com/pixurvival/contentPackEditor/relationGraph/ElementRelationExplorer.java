package com.pixurvival.contentPackEditor.relationGraph;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.map.StaticMapProvider;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.ElementReferenceOrValid;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.reflection.visitor.VisitNode;
import com.pixurvival.core.reflection.visitor.VisitorContext;

import java.lang.reflect.Field;
import java.util.Set;

public class ElementRelationExplorer {

    private VisitorContext visitorContext = new VisitorContext();

    public ElementRelationExplorer() {
        visitorContext.setTraversalCondition(node -> {
            Field field;
            if (node.getKey() instanceof Field) {
                field = (Field) node.getKey();
            } else {
                field = (Field) node.getParent().getKey();
            }
            return field.isAnnotationPresent(Valid.class);
        });
    }

    public void update(NamedIdentifiedElement element, Set<NamedIdentifiedElement> referenceSet, Set<String> resourceReferenceSet) {
        referenceSet.clear();
        resourceReferenceSet.clear();
        visitorContext.visit(element, node -> {
            if (node.getKey() instanceof Field) {
                Field field = (Field) node.getKey();
                ElementReference elementReference = field.getAnnotation(ElementReference.class);
                if (elementReference != null && elementReference.value().equals("") || field.isAnnotationPresent(ElementReferenceOrValid.class) && node.getObject() instanceof NamedIdentifiedElement) {
                    handleElementReferenceNode(referenceSet, node);
                } else if (field.isAnnotationPresent(ResourceReference.class)) {
                    resourceReferenceSet.add((String) node.getObject());
                }
            }
        });
        if (element instanceof StaticMapProvider) {
            resourceReferenceSet.add(((StaticMapProvider) element).getStructuresImageResourceName());
            resourceReferenceSet.add(((StaticMapProvider) element).getTilesImageResourceName());
        }
    }

    private void handleElementReferenceNode(Set<NamedIdentifiedElement> referenceSet, VisitNode node) {
        if (node.getObject() instanceof NamedIdentifiedElement) {
            referenceSet.add((NamedIdentifiedElement) node.getObject());
        } else {
            for (VisitNode child : node.children()) {
                referenceSet.add((NamedIdentifiedElement) child.getObject());
            }
        }
    }
}
