package com.pixurvival.contentPackEditor.component.valueComponent;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.IdentifiedElement;

public class RootElementEditor<E extends IdentifiedElement> extends ElementEditor<E> {

	private static final long serialVersionUID = 1L;

	public RootElementEditor() {
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
	}

	@Override
	public void notifyValueChanged() {
		super.notifyValueChanged();
		EventManager.getInstance().fire(new ElementChangedEvent(getValue(), isValueValid()));
	}
}
