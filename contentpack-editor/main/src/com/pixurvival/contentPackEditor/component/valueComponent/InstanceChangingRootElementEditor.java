package com.pixurvival.contentPackEditor.component.valueComponent;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.ElementType;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.ElementTypeChooseEvent;
import com.pixurvival.contentPackEditor.event.EventListener;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.IdentifiedElement;

public abstract class InstanceChangingRootElementEditor<E extends IdentifiedElement> extends InstanceChangingElementEditor<E> {

	public InstanceChangingRootElementEditor(String translationPreffix) {
		super(translationPreffix);
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
		EventManager.getInstance().register(this);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void notifyValueChanged() {
		super.notifyValueChanged();
		EventManager.getInstance().fire(new ElementChangedEvent(getValue(), isValueValid()));
	}

	@EventListener
	public void elementTypeChooseEvent(ElementTypeChooseEvent event) {
		if (getValue() != null) {
			setValue(getValue());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initialize(E oldInstance, E newInstance) {
		newInstance.setId(oldInstance.getId());
		newInstance.setName(oldInstance.getName());
		// The instance changed, so set the element on the list of the
		// ContentPack
		((List<E>) ContentPackEditionService.getInstance().listOf(ElementType.of(newInstance))).set(newInstance.getId(), newInstance);
	}

}
