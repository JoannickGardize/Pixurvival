package com.pixurvival.contentPackEditor.component.valueComponent;

import lombok.Getter;
import lombok.Setter;

public abstract class NumberInput<T extends Number> extends FormattedTextInput<T> {

	private static final long serialVersionUID = 1L;

	private @Getter @Setter Bounds valueBounds = Bounds.none();

	public NumberInput(Bounds valueBounds) {
		this.valueBounds = valueBounds;
	}

	@Override
	public boolean isValueValid(T value) {
		return super.isValueValid(value) && valueBounds.test(value);
	}
}
