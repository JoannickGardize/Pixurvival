package com.pixurvival.contentPackEditor.component.itemReward;

import java.awt.BorderLayout;

import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.contentPack.item.ItemReward;

public class ItemRewardEditor extends RootElementEditor<ItemReward> {

	private static final long serialVersionUID = 1L;

	public ItemRewardEditor() {
		super(ItemReward.class);
		VerticalListEditor<ItemReward.Entry> entrylistEditor = new VerticalListEditor<>(ItemRewardEntryEditor::new, ItemReward.Entry::new, VerticalListEditor.HORIZONTAL);
		bind(entrylistEditor, "entries");
		setLayout(new BorderLayout());
		add(entrylistEditor, BorderLayout.CENTER);
	}
}
