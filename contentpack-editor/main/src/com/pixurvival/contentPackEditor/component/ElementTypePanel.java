package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.pixurvival.core.contentPack.NamedElement;

public class ElementTypePanel<E extends NamedElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	private ElementList<E> elementList;
	private ElementEditor<E> elementEditor;

	@SuppressWarnings("unchecked")
	public ElementTypePanel(ElementType elementType) {
		elementList = new ElementList<>(elementType);
		setLayout(new BorderLayout());
		add(elementList, BorderLayout.WEST);
		try {
			elementEditor = (ElementEditor<E>) elementType.getElementEditor().newInstance();
			add(elementEditor, BorderLayout.CENTER);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
