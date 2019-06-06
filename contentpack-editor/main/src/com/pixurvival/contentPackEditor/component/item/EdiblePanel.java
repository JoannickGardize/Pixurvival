package com.pixurvival.contentPackEditor.component.item;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.effect.AlterationEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.livingEntity.alteration.Alteration;
import com.pixurvival.core.livingEntity.alteration.InstantDamageAlteration;

public class EdiblePanel extends ItemSpecificPartPanel {

	private static final long serialVersionUID = 1L;

	private ListEditor<Alteration> alterationsEditor = new VerticalListEditor<>(AlterationEditor::new, InstantDamageAlteration::new, ListEditor.HORIZONTAL, true);
	private DoubleInput durationField = new DoubleInput(Bounds.min(0));

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
