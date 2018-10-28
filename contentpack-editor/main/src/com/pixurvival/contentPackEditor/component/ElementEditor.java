package com.pixurvival.contentPackEditor.component;

import javax.swing.JPanel;

import com.pixurvival.core.contentPack.NamedElement;

import lombok.Getter;

public abstract class ElementEditor<E extends NamedElement> extends JPanel {

	private static final long serialVersionUID = 1L;

	private @Getter E editedElement;

	public void setEditedElement(E element) {
		editedElement = element;
		elementChanged(element);
	}

	protected abstract void elementChanged(E element);

}
