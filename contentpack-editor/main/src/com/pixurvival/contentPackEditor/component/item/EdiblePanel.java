package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.effect.AlterationEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.InstantEatAlteration;
import com.pixurvival.core.contentPack.item.EdibleItem;

import javax.swing.*;

public class EdiblePanel extends ItemSpecificPartPanel {

    private static final long serialVersionUID = 1L;

    private ListEditor<Alteration> alterationsEditor = new VerticalListEditor<>(AlterationEditor::new, InstantEatAlteration::new, ListEditor.HORIZONTAL, true);
    private TimeInput durationField = new TimeInput();

    public EdiblePanel() {

        EventManager.getInstance().register(this);

        JPanel propertiesPanel = LayoutUtils.createVerticalLabelledBox("generic.duration", durationField);
        propertiesPanel.setBorder(LayoutUtils.createGroupBorder("generic.properties"));

        alterationsEditor.setBorder(LayoutUtils.createGroupBorder("effectTargetEditor.alterations"));
        LayoutUtils.addVertically(this, LayoutUtils.DEFAULT_GAP, 1, propertiesPanel, alterationsEditor);

    }

    @Override
    public void bindTo(ItemEditor itemEditor) {
        itemEditor.bind(durationField, "duration", EdibleItem.class);
        itemEditor.bind(alterationsEditor, "alterations", EdibleItem.class);
    }

}
