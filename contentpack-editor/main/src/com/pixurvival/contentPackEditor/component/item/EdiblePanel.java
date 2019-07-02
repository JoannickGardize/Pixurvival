package com.pixurvival.contentPackEditor.component.item;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.effect.AlterationEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.TimeInput;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.InstantEatAlteration;

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
		itemEditor.bind(durationField, EdibleItem::getDuration, EdibleItem::setDuration, EdibleItem.class);
		itemEditor.bind(alterationsEditor, EdibleItem::getAlterations, EdibleItem::setAlterations, EdibleItem.class);

	}

}
