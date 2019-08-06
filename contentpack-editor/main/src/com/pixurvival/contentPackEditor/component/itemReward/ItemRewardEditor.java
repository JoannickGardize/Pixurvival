package com.pixurvival.contentPackEditor.component.itemReward;

import java.awt.BorderLayout;

import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.item.ItemReward;

public class ItemRewardEditor extends RootElementEditor<ItemReward> {

	private static final long serialVersionUID = 1L;

	private VerticalListEditor<ItemReward.Entry> entrylistEditor = new VerticalListEditor<>(ItemRewardEntryEditor::new, ItemReward.Entry::new);

	public ItemRewardEditor() {

		bind(entrylistEditor, ItemReward::getEntries, ItemReward::setEntries);

		setLayout(new BorderLayout());
		add(entrylistEditor, BorderLayout.CENTER);
	}
}
