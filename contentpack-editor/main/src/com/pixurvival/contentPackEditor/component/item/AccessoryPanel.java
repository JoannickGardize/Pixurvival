package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.item.AccessoryItem;

public class AccessoryPanel extends EquipablePanel {

	private static final long serialVersionUID = 1L;

	private ItemAlterationAbilityEditor abilityEditor = new ItemAlterationAbilityEditor(true);

	public AccessoryPanel() {
		super(false);
		EventManager.getInstance().register(abilityEditor);
		abilityEditor.setBorder(LayoutUtils.createGroupBorder("accessoryEditor.ability"));
		finalizeLayout(abilityEditor);
	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		bindTo(itemEditor, AccessoryItem.class);
		itemEditor.bind(abilityEditor, "ability", AccessoryItem.class);
	}

}
