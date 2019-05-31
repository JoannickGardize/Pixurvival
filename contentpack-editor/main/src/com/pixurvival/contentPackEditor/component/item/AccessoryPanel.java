package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.abilitySet.EffectAbilityEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.item.AccessoryItem;

public class AccessoryPanel extends EquipablePanel {

	private static final long serialVersionUID = 1L;

	private EffectAbilityEditor abilityEditor = new EffectAbilityEditor(true, true);

	public AccessoryPanel() {
		EventManager.getInstance().register(abilityEditor);
		abilityEditor.setBorder(LayoutUtils.createGroupBorder("accessoryEditor.ability"));
		finalizeLayout(abilityEditor);
	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		super.bindTo(itemEditor);
		itemEditor.bind(abilityEditor, AccessoryItem::getAbility, AccessoryItem::setAbility, AccessoryItem.class);
	}

}
