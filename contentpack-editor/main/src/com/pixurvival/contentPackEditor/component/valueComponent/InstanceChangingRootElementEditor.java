package com.pixurvival.contentPackEditor.component.valueComponent;

import com.pixurvival.contentPackEditor.ContentPackEditionService;
import com.pixurvival.contentPackEditor.event.ElementChangedEvent;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

public abstract class InstanceChangingRootElementEditor<E extends NamedIdentifiedElement> extends InstanceChangingElementEditor<E> {

    private static final long serialVersionUID = 1L;

    public InstanceChangingRootElementEditor(Class<? super E> type, String translationPreffix) {
        super(type, translationPreffix, null);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
    }

    @Override
    public void notifyValueChanged() {
        super.notifyValueChanged();
        EventManager.getInstance().fire(new ElementChangedEvent(getValue(), isValueValid()));
    }

    @Override
    protected void initialize(E oldInstance, E newInstance) {
        newInstance.setId(oldInstance.getId());
        newInstance.setName(oldInstance.getName());
        // The instance changed, so set the element on the list of the
        // ContentPack
        ContentPackEditionService.getInstance().changeInstance(newInstance);
    }
}
