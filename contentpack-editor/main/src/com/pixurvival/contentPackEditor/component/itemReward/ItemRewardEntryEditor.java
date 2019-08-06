package com.pixurvival.contentPackEditor.component.itemReward;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.core.contentPack.item.ItemReward;

import lombok.Getter;

public class ItemRewardEntryEditor extends ElementEditor<ItemReward.Entry> {

	private static final long serialVersionUID = 1L;

	private @Getter ItemStackEditor itemStackEditor = new ItemStackEditor();

	public ItemRewardEntryEditor() {
		DoubleInput probabilityInput = new DoubleInput(new Bounds(0, 1));

		bind(itemStackEditor, ItemReward.Entry::getItemStack, ItemReward.Entry::setItemStack);
		bind(probabilityInput, ItemReward.Entry::getProbability, ItemReward.Entry::setProbability);

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