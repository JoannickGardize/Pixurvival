package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ElementType;
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
			elementEditor.setVisible(false);
			add(elementEditor, BorderLayout.CENTER);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		elementList.addListSelectionListener(e -> {
			E element = elementList.getSelectedElement();
			elementEditor.setVisible(element != null);
			if (element != null) {
				elementEditor.setValue(element);
			}
		});
	}
}
