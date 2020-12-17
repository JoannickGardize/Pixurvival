package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.ElementReferenceOrValid;
import com.pixurvival.core.reflection.visitor.VisitNode;
import com.pixurvival.core.util.Cache;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ElementReferenceHandler implements AnnotationHandler {

	@Getter
	@AllArgsConstructor
	private static class Entry {
		private VisitNode node;
		private Annotation annotation;
	}

	private Cache<String, NodePath> nodePathCache = new Cache<>(NodePath::of);

	private Collection<Entry> entries;

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Arrays.asList(ElementReference.class, ElementReferenceOrValid.class);
	}

	@Override
	public void begin() {
		entries = new ArrayList<>();
	}

	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		if (!(annotation instanceof ElementReferenceOrValid) || node.getObject() instanceof IdentifiedElement) {
			entries.add(new Entry(node, annotation));
		}
	}

	@Override
	public void end(ErrorCollection errors) {
		for (Entry entry : entries) {
			validateNode(entry.getNode(), entry.getAnnotation(), errors);
		}
		entries = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void validateNode(VisitNode node, Annotation annotation, ErrorCollection errors) {
		List<IdentifiedElement> elementList;
		if (annotation instanceof ElementReference && !((ElementReference) annotation).value().equals("")) {
			try {
				elementList = (List<IdentifiedElement>) nodePathCache.get(((ElementReference) annotation).value()).apply(node.getParent()).getObject();
			} catch (NodePathException e) {
				Log.debug("NodePath execution failed", e);
				errors.add(node, annotation);
				return;
			}
		} else {
			elementList = ((ContentPack) node.getRoot().getObject()).listOf((Class) node.getObject().getClass());
		}
		if (node.getKey() instanceof Field) {
			validateElement(node, annotation, errors, elementList);
		} else {
			// It's a Collection or a Map
			for (VisitNode childNode : node.children()) {
				validateElement(childNode, annotation, errors, elementList);
			}
		}
	}

	private void validateElement(VisitNode node, Annotation annotation, ErrorCollection errors, List<IdentifiedElement> elementList) {
		IdentifiedElement element = (IdentifiedElement) node.getObject();
		if (elementList == null || elementList.size() <= element.getId() || elementList.get(element.getId()) != element) {
			errors.add(node, annotation);
		}
	}
}
