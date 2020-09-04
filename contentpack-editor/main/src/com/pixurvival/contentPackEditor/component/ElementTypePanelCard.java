package com.pixurvival.contentPackEditor.component;

import java.awt.CardLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.event.ElementSelectedEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.contentPackEditor.event.ResourceListChangedEvent;

public class ElementTypePanelCard extends JPanel {

	public static final String NONE_CARD = "NONE";

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	private ElementEditor currentEditor;

	public ElementTypePanelCard() {
		setLayout(new CardLayout());
		add(new JPanel(), NONE_CARD);
		for (ElementType elementType : ElementType.values()) {
			add(ContentPackEditionService.getInstance().editorOf(elementType), elementType.name());
		}
		((CardLayout) getLayout()).show(this, NONE_CARD);
		EventManager.getInstance().register(this);
	}

	@SuppressWarnings("unchecked")
	@EventListener
	public void elementSelected(ElementSelectedEvent event) {
		ElementType type = ElementType.of(event.getElement());
		((CardLayout) getLayout()).show(this, type.name());
		currentEditor = ContentPackEditionService.getInstance().editorOf(type);
		ContentPackEditionService.getInstance().editorOf(type).setValue(event.getElement());
	}

	@SuppressWarnings("unchecked")
	@EventListener
	public void resourceListChanged(ResourceListChangedEvent event) {
		if (currentEditor != null) {
			currentEditor.setValue(currentEditor.getValue());
		}
	}
}