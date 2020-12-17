package com.pixurvival.core.contentPack.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;

/**
 * <p>
 * Must annotate a field of a sub-type of {@link IdentifiedElement}. <br>
 * Indicates that the given field is a reference of an element contained in a
 * list of {@link IdentifiedElement} elsewhere, in the index
 * {@link IdentifiedElement#getId()}. This element list should be annotated with
 * {@link ElementList}.
 * <p>
 * The value indicates the relative path of the list through object graph,
 * starting at the object that declared this annotation.<br>
 * The default empty string value means that the list is an element list in the
 * {@link ContentPack} root object.<br>
 * "<" means parent object, "." means that the following word is the name of a
 * field of the current object.
 * 
 * @author SharkHendrix
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElementReference {
	String value() default "";
}
