package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pixurvival.core.contentPack.IdentityHolder;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.contentPack.validation.annotation.Unique;
import com.pixurvival.core.reflection.visitor.VisitNode;

public class UniqueHandler implements AnnotationHandler {

	@Override
	public Collection<Class<? extends Annotation>> getHandledAnnotations() {
		return Collections.singleton(Unique.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(VisitNode node, Annotation annotation, ErrorCollection errors) {
		if (!test((List<IdentityHolder>) node.getObject())) {
			errors.add(node, annotation);
		}
	}

	public static boolean test(List<IdentityHolder> list) {
		Set<Object> set = new HashSet<>();
		for (IdentityHolder element : list) {
			if (set.contains(element.getIdentifier())) {
				return false;
			} else {
				set.add(element.getIdentifier());
			}
		}
		return true;
	}
}
