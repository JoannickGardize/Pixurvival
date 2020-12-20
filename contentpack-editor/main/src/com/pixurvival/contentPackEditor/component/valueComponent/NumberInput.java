package com.pixurvival.contentPackEditor.component.valueComponent;

import java.lang.annotation.Annotation;

import com.pixurvival.core.contentPack.validation.annotation.Positive;

import lombok.Getter;
import lombok.Setter;

public abstract class NumberInput<T extends Number> extends FormattedTextInput<T> {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter Bounds valueBounds = Bounds.none();

	@Override
	public boolean isValueValid(T value) {
		return super.isValueValid(value) && valueBounds.test(value);
	}

	@Override
	public void configure(Annotation annotation) {
		if (annotation instanceof com.pixurvival.core.contentPack.validation.annotation.Bounds) {
			com.pixurvival.core.contentPack.validation.annotation.Bounds bounds = (com.pixurvival.core.contentPack.validation.annotation.Bounds) annotation;
			valueBounds = new Bounds(bounds.min(), !bounds.minInclusive(), bounds.max(), !bounds.maxInclusive());
		} else if (annotation instanceof Positive) {
			valueBounds = Bounds.positive();
		}
	}
}
