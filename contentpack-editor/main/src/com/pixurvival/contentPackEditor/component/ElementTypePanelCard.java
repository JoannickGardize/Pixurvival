package com.pixurvival.contentPackEditor.component;

import java.awt.CardLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.event.ElementTypeChooseEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;

public class ElementTypePanelCard extends JPanel {

	public static final String NONE_CARD = "NONE";

	private static final long serialVersionUID = 1L;

	public ElementTypePanelCard() {
		setLayout(new CardLayout());
		add(new JPanel(), NONE_CARD);
		for (ElementType elementType : ElementType.values()) {
			add(new ElementTypePanel<>(elementType), elementType.name());
		}
		((CardLayout) getLayout()).show(this, NONE_CARD);
		EventManager.getInstance().register(this);
	}

	@EventListener
	public void elementTypeChoose(ElementTypeChooseEvent event) {
		((CardLayout) getLayout()).show(this, event.getElementType().name());
	}
}
