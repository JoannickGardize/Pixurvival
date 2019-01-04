package com.pixurvival.contentPackEditor.component;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.NamedElement;

public class RootElementEditor<E extends NamedElement> extends ElementEditor<E> {

	private static final long serialVersionUID = 1L;

	public RootElementEditor() {
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
	}

	@Override
	protected void notifyValueChanged() {
		super.notifyValueChanged();
		EventManager.getInstance().fire(new ElementChangedEvent(getValue(), isValueValid()));
	}

}
