package com.pixurvival.contentPackEditor.component.item;

import javax.swing.JPanel;

public abstract class ItemSpecificPartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Bind to the itemEditor all the specific attributes.
	 * 
	 * @param itemEditor
	 */
	public abstract void bindTo(ItemEditor itemEditor);
}
