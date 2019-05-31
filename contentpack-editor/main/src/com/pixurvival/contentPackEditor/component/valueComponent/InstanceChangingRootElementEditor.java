package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import com.pixurvival.contentPackEditor.event.ElementTypeChooseEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.core.contentPack.IdentifiedElement;

public class InstanceChangingRootElementEditor<E extends IdentifiedElement> extends InstanceChangingElementEditor<E> {

	public InstanceChangingRootElementEditor(String translationPreffix) {
		super(translationPreffix);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
		// EventManager.getInstance().register(this);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void notifyValueChanged() {
		super.notifyValueChanged();
		// EventManager.getInstance().fire(new ElementChangedEvent(getValue(),
		// isValueValid()));
	}

	@EventListener
	public void elementTypeChooseEvent(ElementTypeChooseEvent event) {
		if (getValue() != null) {
			setValue(getValue());
		}
	}

	@Override
	protected List<InstanceChangingElementEditor<E>.ClassEntry> getClassEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initialize(E oldInstance, E newInstance) {
		// TODO Auto-generated method stub
	}

}
