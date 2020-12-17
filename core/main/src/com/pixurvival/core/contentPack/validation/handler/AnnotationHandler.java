package com.pixurvival.core.contentPack.validation.handler;

import java.lang.annotation.Annotation;
import java.util.Collection;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.validation.ContentPackValidator;
import com.pixurvival.core.contentPack.validation.ErrorCollection;
import com.pixurvival.core.reflection.visitor.VisitNode;

/**
 * Interface of annotation handlers of the {@link ContentPackValidator}.
 * 
 * @author SharkHendrix
 *
 */
public interface AnnotationHandler {

	/**
	 * Called to bind this handler to annotations.
	 * 
	 * @return a collection containing all the annotation types this handler wants
	 *         to handle.
	 */
	Collection<Class<? extends Annotation>> getHandledAnnotations();

	/**
	 * Handle the given node.
	 * 
	 * @param node
	 *            the node to check, it's root value is an instance of
	 *            {@link ContentPack}
	 * @param annotation
	 *            the annotation for which this handler is called, this is always an
	 *            instance of annotation for which it's type is returned by
	 *            {@link #getHandledAnnotations()}
	 * @param errors
	 *            the error collection to add validation errors in
	 */
	void handle(VisitNode node, Annotation annotation, ErrorCollection errors);

	/**
	 * Called at the beginning of a ContentPack validation, for optional
	 * initialization.
	 */
	default void begin() {
		// Nothing by default
	}

	/**
	 * Called at the end of a ContentPack validation, for optional finalization.
	 * 
	 * @param errors
	 *            the error collection to add validation errors inS
	 */
	default void end(ErrorCollection errors) {
		// Nothing by default
	}
}
