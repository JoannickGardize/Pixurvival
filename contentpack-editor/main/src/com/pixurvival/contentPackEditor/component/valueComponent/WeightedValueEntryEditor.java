package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.Collection;
import java.util.function.Function;

import javax.swing.Icon;

import com.pixurvival.contentPackEditor.component.elementChooser.ElementChooserButton;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.WeightedValueProducer.Entry;

public class WeightedValueEntryEditor<E extends IdentifiedElement> extends ElementEditor<Entry<E>> {

	private static final long serialVersionUID = 1L;

	private ElementChooserButton<E> elementChooser;

	public WeightedValueEntryEditor(Function<E, Icon> iconProvider) {

		elementChooser = new ElementChooserButton<>(iconProvider);
		DoubleInput probability = new DoubleInput(Bounds.positive());

		bind(elementChooser, Entry::getElement, Entry::setElement);
		bind(probability, Entry::getProbability, Entry::setProbability);

		add(elementChooser);
		add(probability);
	}

	public void setCollection(Collection<E> collection) {
		elementChooser.setItems(collection);
	}
}
