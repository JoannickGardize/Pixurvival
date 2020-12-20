package com.pixurvival.contentPackEditor.component.itemReward;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.PercentInput;
import com.pixurvival.core.contentPack.item.ItemReward;

import lombok.Getter;

public class ItemRewardEntryEditor extends ElementEditor<ItemReward.Entry> {

	private static final long serialVersionUID = 1L;

	private @Getter ItemStackEditor itemStackEditor = new ItemStackEditor();

	public ItemRewardEntryEditor() {
		super(ItemReward.Entry.class);
		PercentInput probabilityInput = new PercentInput();

		bind(itemStackEditor, "itemStack");
		bind(probabilityInput, "probability");

		setLayout(new GridBagLayout());

		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		gbc.weightx = 2;
		gbc.fill = GridBagConstraints.BOTH;
		add(itemStackEditor, gbc);
		gbc.gridx++;
		gbc.insets.left = 10;
		LayoutUtils.addHorizontalLabelledItem(this, "generic.probability", probabilityInput, gbc);
	}
}
