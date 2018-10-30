package com.pixurvival.contentPackEditor.component;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.event.ElementTypeChooseEvent;
import com.pixurvival.contentPackEditor.event.EventManager;

public class ElementTypeChooser extends JList<ElementType> {

	private static final long serialVersionUID = 1L;

	public ElementTypeChooser() {
		super(ElementType.values());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				EventManager.getInstance().fire(new ElementTypeChooseEvent(getSelectedValue()));
			}
		});
	}
}
