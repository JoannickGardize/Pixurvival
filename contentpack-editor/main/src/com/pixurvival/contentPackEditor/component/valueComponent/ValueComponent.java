package com.pixurvival.contentPackEditor.component.valueComponent;

import javax.swing.JLabel;

public interface ValueComponent<T> {

	T getValue();

	/**
	 * Does not notify {@link ValueChangeListener}s
	 * 
	 * @param value
	 */
	void setValue(T value);

	default boolean isValueValid() {
		return isValueValid(getValue());
	}

	boolean isValueValid(T value);

	void setAssociatedLabel(JLabel label);

	JLabel getAssociatedLabel();

	void addValueChangeListener(ValueChangeListener<T> listener);
}
