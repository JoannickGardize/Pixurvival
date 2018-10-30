package com.pixurvival.contentPackEditor.component.util;

import javax.swing.JLabel;

public interface ValueComponent<T> {

	T getValue();

	/**
	 * Does not notify {@link ValueChangeListener}s
	 * 
	 * @param value
	 */
	void setValue(T value);

	boolean isValueValid();

	void setAssociatedLabel(JLabel label);

	JLabel getAssociatedLabel();

	void addValueChangeListener(ValueChangeListener<T> listener);
}
