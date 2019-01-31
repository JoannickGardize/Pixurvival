package com.pixurvival.core.contentPack.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementCollection;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Pattern;
import com.pixurvival.core.contentPack.validation.annotation.Required;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.reflection.visitor.VisitNode;
import com.pixurvival.core.reflection.visitor.VisitorContext;

import lombok.val;

public class ContentPackValidator {

	private static interface AnnotationHandler {
		void handle(ContentPackValidator instance, VisitNode node, Annotation annotation);
	}

	private static final Map<Class<? extends Annotation>, AnnotationHandler> annotationHandlers = new HashMap<>();

	private List<InvalidNode> invalidNodes;
	private Map<Class<? extends IdentifiedElement>, List<IdentifiedElement>> rootElementsLists = new HashMap<>();
	private List<VisitNode> rootElementsReferences = new ArrayList<>();

	static {
		annotationHandlers.put(Bounds.class, ContentPackValidator::handleBounds);
		annotationHandlers.put(ElementCollection.class, ContentPackValidator::handleElementCollection);
		annotationHandlers.put(ElementReference.class, ContentPackValidator::handleElementReference);
		annotationHandlers.put(Required.class, ContentPackValidator::handleRequired);
		annotationHandlers.put(Pattern.class, ContentPackValidator::handlePattern);
		annotationHandlers.put(Length.class, ContentPackValidator::handleLength);
		annotationHandlers.put(ResourceReference.class, ContentPackValidator::handleResourceReference);
	}

	public List<InvalidNode> validate(ContentPack contentPack) {
		invalidNodes = new ArrayList<>();
		rootElementsLists.clear();
		rootElementsReferences.clear();
		VisitorContext.getInstance().setTraversalAnnotation(Valid.class);
		VisitorContext.getInstance().visit(contentPack, this::visit);
		for (VisitNode node : rootElementsReferences) {
			validateElementReference(rootElementsLists.get(node.getObject().getClass()), node);
		}
		return invalidNodes;
	}

	public List<InvalidNode> validate(IdentifiedElement element) {
		invalidNodes = new ArrayList<>();
		rootElementsReferences.clear();
		VisitorContext.getInstance().visit(element, this::visit);
		for (VisitNode node : rootElementsReferences) {
			validateElementReference(rootElementsLists.get(node.getObject().getClass()), node);
		}
		return invalidNodes;

	}

	private boolean visit(VisitNode node) {
		if (node.getKey() instanceof Field) {
			Field field = (Field) node.getKey();
			for (Annotation annotation : field.getAnnotations()) {
				AnnotationHandler handler = annotationHandlers.get(annotation.annotationType());
				if (handler != null) {
					handler.handle(this, node, annotation);
				}
			}
		}
		return true;
	}

	private void handleBounds(VisitNode node, Annotation annotation) {
		Bounds bounds = (Bounds) annotation;
		double value = ((Number) node.getObject()).doubleValue();
		if (value < bounds.min() || value > bounds.max() || !bounds.minInclusive() && value == bounds.min() || !bounds.maxInclusive() && value == bounds.max()) {
			invalidNodes.add(new InvalidNode(node, bounds));
		}
	}

	@SuppressWarnings("unchecked")
	private void handleElementCollection(VisitNode node, Annotation annotation) {
		ElementCollection elementCollection = (ElementCollection) annotation;
		rootElementsLists.put((Class<IdentifiedElement>) elementCollection.value(), (List<IdentifiedElement>) node.getObject());
		validateElementCollection(node, elementCollection);
	}

	private void validateElementCollection(VisitNode node, ElementCollection elementCollection) {
		if (node.getObject() == null) {
			invalidNodes.add(new InvalidNode(node, InvalidCause.ELEMENT_LIST_NULL));
			return;
		}
		val list = (List<?>) node.getObject();
		for (int i = 0; i < list.size(); i++) {
			Object element = list.get(i);
			if (((IdentifiedElement) element).getId() != i) {
				invalidNodes.add(new InvalidNode(node, elementCollection));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleElementReference(VisitNode node, Annotation annotation) {
		if (node.getObject() == null) {
			return;
		}
		ElementReference elementReference = (ElementReference) annotation;
		if (elementReference.depth() == 0) {
			rootElementsReferences.add(node);
		} else {
			VisitNode listNode = findReferencedListNode(node, elementReference);
			validateElementReference((List<IdentifiedElement>) listNode.getObject(), listNode);
		}
	}

	private VisitNode findReferencedListNode(VisitNode node, ElementReference elementReference) {
		return node.getAncestor(elementReference.depth()).findChild(child -> {
			if (child.getKey() instanceof Field) {
				ElementCollection elementCollection = ((Field) child.getKey()).getAnnotation(ElementCollection.class);
				return elementCollection != null && elementCollection.value() == node.getObject().getClass();
			}
			return false;
		});
	}

	private void validateElementReference(List<IdentifiedElement> list, VisitNode node) {
		if (node.getObject() == null) {
			return;
		}
		if (list == null) {
			invalidNodes.add(new InvalidNode(node, InvalidCause.ELEMENT_LIST_NOT_FOUND));
			return;
		}
		if (list.get(((IdentifiedElement) node.getObject()).getId()) != node.getObject()) {
			invalidNodes.add(new InvalidNode(node, InvalidCause.NOT_REFERENCED_ELEMENT));
		}
	}

	private void handleRequired(VisitNode node, Annotation annotation) {
		if (node.getObject() == null) {
			invalidNodes.add(new InvalidNode(node, annotation));
		}
	}

	private void handlePattern(VisitNode node, Annotation annotation) {
		Pattern pattern = (Pattern) annotation;
		if (node.getObject() != null && !node.getObject().toString().matches(pattern.value())) {
			invalidNodes.add(new InvalidNode(node, pattern));
		}
	}

	private void handleLength(VisitNode node, Annotation annotation) {
		Length length = (Length) annotation;
		Object object = node.getObject();
		if (object != null) {
			int actualLength = 0;
			if (object instanceof String) {
				actualLength = ((String) object).length();
			} else if (object instanceof List) {
				actualLength = ((List<?>) object).size();
			} else if (object instanceof Map) {
				actualLength = ((Map<?, ?>) object).size();
			} else {
				throw new IllegalStateException();
			}
			if (actualLength < length.min() || actualLength >= length.max()) {
				invalidNodes.add(new InvalidNode(node, length));
			}
		}
	}

	private void handleResourceReference(VisitNode node, Annotation annotation) {
		if (node.getObject() == null) {
			return;
		}
		ResourceReference resourceReference = (ResourceReference) annotation;
		ContentPack contentPack = (ContentPack) node.getRoot().getObject();
		if (!contentPack.isResourcePresent((String) node.getObject())) {
			invalidNodes.add(new InvalidNode(node, resourceReference));
		}
	}
}
