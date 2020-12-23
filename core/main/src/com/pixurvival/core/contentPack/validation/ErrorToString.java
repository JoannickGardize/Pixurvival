package com.pixurvival.core.contentPack.validation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.pixurvival.core.contentPack.validation.annotation.AnimationTemplateRequirement;
import com.pixurvival.core.contentPack.validation.annotation.Bounds;
import com.pixurvival.core.contentPack.validation.annotation.ElementList;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.ElementReferenceOrValid;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Pattern;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.ResourceReference;
import com.pixurvival.core.contentPack.validation.annotation.SpriteHeight;
import com.pixurvival.core.contentPack.validation.annotation.SpriteWidth;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorToString {

	private static final Map<Class<?>, Function<Object, String>> TO_STRING_FUNCTIONS = new HashMap<>();

	static {
		addToStringFunction(Bounds.class, b -> {
			StringBuilder sb = new StringBuilder("the value must be ");
			if (b.min() != Float.NEGATIVE_INFINITY) {
				sb.append(b.minInclusive() ? ">= " : "> ").append(b.min());
				if (b.max() != Float.POSITIVE_INFINITY) {
					sb.append(" and ");
				}
			}
			if (b.max() != Float.POSITIVE_INFINITY) {
				sb.append(b.maxInclusive() ? "<= " : "< ").append(b.max());
			}
			return sb.toString();
		});
		addToStringFunction(ElementList.class, e -> "the element list is wrongly indexed, or is missing");
		addToStringFunction(ElementReference.class, e -> "the element has not been found in its reference list");
		addToStringFunction(ElementReferenceOrValid.class, e -> "the element has not been found in its reference list");
		addToStringFunction(Length.class, l -> {
			StringBuilder sb = new StringBuilder("the element must have a length ");
			if (l.min() > 0) {
				sb.append(">= ").append(l.min());
				if (l.max() < Integer.MAX_VALUE) {
					sb.append(" and ");
				}
			}
			if (l.max() < Integer.MAX_VALUE) {
				sb.append("< ").append(l.max());
			}
			return sb.toString();
		});
		addToStringFunction(NullErrorCause.class, n -> "the element is missing");
		addToStringFunction(Pattern.class, p -> "the character sequence must match the pattern " + p.value());
		addToStringFunction(Positive.class, p -> "the value cannot be negative");
		addToStringFunction(ResourceReference.class, p -> "the resource does not exists or is not an image");
		addToStringFunction(SpriteWidth.class, p -> "The sprite width does not divide correctly the image width");
		addToStringFunction(SpriteHeight.class, p -> "The sprite height does not divide correctly the image height");
		addToStringFunction(SpriteHeight.class, p -> "The sprite height does not divide correctly the image height");
		addToStringFunction(AnimationTemplateRequirement.class, a -> "The Animtation Template of the Sprite Sheet must contains at least the following actions: " + a.value());
	}

	public static String toString(Object cause) {
		return TO_STRING_FUNCTIONS.get(cause instanceof Annotation ? ((Annotation) cause).annotationType() : cause.getClass()).apply(cause);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> void addToStringFunction(Class<T> type, Function<T, String> function) {
		TO_STRING_FUNCTIONS.put(type, (Function) function);
	}
}
