package com.pixurvival.contentPackEditor.component.valueComponent;

import java.awt.Component;
import java.io.Serializable;

import com.pixurvival.core.contentPack.WeightedValueProducer.Entry;

public class WeightedValueEntryEditor<E extends Serializable> extends ElementEditor<Entry<E>> {

	private static final long serialVersionUID = 1L;

	public WeightedValueEntryEditor(ValueComponent<E> elementEditor) {

		FloatInput probability = new FloatInput(Bounds.positive());

		bind(elementEditor, Entry::getElement, Entry::setElement);
		bind(probability, Entry::getProbability, Entry::setProbability);

		add((Component) elementEditor);
		add(probability);
	}
}
