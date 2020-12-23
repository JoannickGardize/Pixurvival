package com.pixurvival.contentPackEditor.component.valueComponent;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

import com.pixurvival.contentPackEditor.component.valueComponent.constraint.BoundsConstraint;
import com.pixurvival.core.contentPack.validation.annotation.Positive;

import lombok.Getter;
import lombok.Setter;

public abstract class NumberInput<T extends Number> extends FormattedTextInput<T> {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter Predicate<Number> constraint = n -> true;

	@Override
	public boolean isValueValid(T value) {
		return super.isValueValid(value) && constraint.test(value);
	}

	@Override
	public void configure(Annotation annotation) {
		if (annotation instanceof com.pixurvival.core.contentPack.validation.annotation.Bounds) {
			com.pixurvival.core.contentPack.validation.annotation.Bounds bounds = (com.pixurvival.core.contentPack.validation.annotation.Bounds) annotation;
			constraint = new BoundsConstraint(bounds.min(), !bounds.minInclusive(), bounds.max(), !bounds.maxInclusive());
		} else if (annotation instanceof Positive) {
			constraint = BoundsConstraint.positive();
		}
	}
}
