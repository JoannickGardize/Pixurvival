package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.Collection;
import java.util.function.Function;

import javax.swing.Icon;

import com.pixurvival.contentPackEditor.FileService;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.WeightedValueProducer.Entry;

public class WeightedValueProducerEditor<E extends IdentifiedElement> extends ElementEditor<WeightedValueProducer<E>> {

	private static final long serialVersionUID = 1L;

	private Function<E, Icon> iconProvider;
	private Function<ContentPack, Collection<E>> itemsGetter;

	private ListEditor<Entry<E>> structureGeneratorEntriesEditor = new HorizontalListEditor<>(() -> {
		WeightedValueEntryEditor<E> editor = new WeightedValueEntryEditor<>(iconProvider);
		editor.setBorder(LayoutUtils.createBorder());
		ContentPack currentPack = FileService.getInstance().getCurrentContentPack();
		if (currentPack != null) {
			editor.setCollection(itemsGetter.apply(currentPack));
		}
		return editor;

	}, Entry::new);

	public WeightedValueProducerEditor(Function<E, Icon> iconProvider, Function<ContentPack, Collection<E>> itemsGetter) {
		this.iconProvider = iconProvider;
		this.itemsGetter = itemsGetter;

		bind(structureGeneratorEntriesEditor, WeightedValueProducer::getBackingArray, WeightedValueProducer::setBackingArray);

		LayoutUtils.fill(this, structureGeneratorEntriesEditor);
	}

	public void setAllItems(Collection<E> items) {
		structureGeneratorEntriesEditor.forEachEditors(e -> ((WeightedValueEntryEditor<E>) e).setCollection(items));
	}

}
