package com.pixurvival.contentPackEditor.component;

import java.awt.CardLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.event.ElementTypeChooseEvent;
import com.pixurvival.contentPackEditor.event.EventHandler;
import com.pixurvival.contentPackEditor.event.EventManager;

public class ElementTypePanelCard extends JPanel {

	private static final long serialVersionUID = 1L;

	public ElementTypePanelCard() {
		setLayout(new CardLayout());
		for (ElementType elementType : ElementType.values()) {
			add(new ElementTypePanel<>(elementType), elementType.name());
		}
		EventManager.getInstance().register(this);

	}

	@EventHandler
	public void elementTypeChoose(ElementTypeChooseEvent event) {
		((CardLayout) getLayout()).show(this, event.getElementType().name());
	}
}
