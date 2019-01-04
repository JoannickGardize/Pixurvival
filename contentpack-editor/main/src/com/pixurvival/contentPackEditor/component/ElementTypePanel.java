package com.pixurvival.contentPackEditor.component;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.core.contentPack.NamedElement;

public class ElementTypePanel<E extends NamedElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	private ElementList<E> elementList;
	private ElementEditor<E> elementEditor;

	@SuppressWarnings("unchecked")
	public ElementTypePanel(ElementType elementType) {
		elementList = (ElementList<E>) elementType.getElementList();
		setLayout(new BorderLayout());
		add(elementList, BorderLayout.WEST);
		elementEditor = elementType.getElementEditor();
		elementEditor.setVisible(false);
		add(elementEditor, BorderLayout.CENTER);
		elementList.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}
			E element = elementList.getSelectedElement();
			elementEditor.setVisible(element != null);
			if (element != null) {
				elementEditor.setValue(element);
			}
		});
	}
}
