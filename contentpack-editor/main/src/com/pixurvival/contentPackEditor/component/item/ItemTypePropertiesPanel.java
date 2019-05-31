package com.pixurvival.contentPackEditor.component.item;

import javax.swing.JPanel;

public abstract class ItemTypePropertiesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public abstract void bindTo(ItemEditor itemEditor);
}
