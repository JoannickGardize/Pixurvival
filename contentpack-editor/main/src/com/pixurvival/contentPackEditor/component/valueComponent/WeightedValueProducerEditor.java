package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.Collection;
import java.util.function.Function;

import javax.swing.Icon;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.WeightedValueProducer.Entry;

public class WeightedValueProducerEditor<E extends IdentifiedElement> extends ElementEditor<WeightedValueProducer<E>> {

	private static final long serialVersionUID = 1L;

	private ListEditor<Entry<E>> structureGeneratorEntriesEditor;

	public WeightedValueProducerEditor(Class<E> elementType, Function<E, Icon> iconProvider, Function<ContentPack, Collection<E>> itemsGetter) {
		structureGeneratorEntriesEditor = new HorizontalListEditor<>(() -> {
			WeightedValueEntryEditor<E> editor = new WeightedValueEntryEditor<>(elementType, iconProvider);
			editor.setBorder(LayoutUtils.createBorder());
			return editor;

		}, Entry::new);

		bind(structureGeneratorEntriesEditor, WeightedValueProducer::getBackingArray, WeightedValueProducer::setBackingArray);

		LayoutUtils.fill(this, structureGeneratorEntriesEditor);
	}
}
