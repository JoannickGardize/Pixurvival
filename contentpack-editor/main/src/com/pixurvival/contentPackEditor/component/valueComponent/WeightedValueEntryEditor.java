package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.WeightedValueProducer.Entry;

public class WeightedValueEntryEditor<E extends IdentifiedElement> extends ElementEditor<Entry<E>> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<E> elementChooser;

	public WeightedValueEntryEditor(Class<E> elementType) {

		elementChooser = new ElementChooserButton<>(elementType);
		FloatInput probability = new FloatInput(Bounds.positive());

		bind(elementChooser, Entry::getElement, Entry::setElement);
		bind(probability, Entry::getProbability, Entry::setProbability);

		add(elementChooser);
		add(probability);
	}
}
