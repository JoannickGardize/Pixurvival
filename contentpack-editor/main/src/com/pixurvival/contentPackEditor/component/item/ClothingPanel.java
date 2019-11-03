package com.pixurvival.contentPackEditor.component.item;

import javax.swing.JPanel;

import com.pixurvival.core.contentPack.item.ClothingItem;

public class ClothingPanel extends EquipablePanel {

	private static final long serialVersionUID = 1L;

	public ClothingPanel() {
		super(true);
		finalizeLayout(new JPanel());
	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		bindTo(itemEditor, ClothingItem.class);
	}

}
