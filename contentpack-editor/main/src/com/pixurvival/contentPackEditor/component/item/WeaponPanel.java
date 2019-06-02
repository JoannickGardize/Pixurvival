package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.abilitySet.EffectAbilityEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.contentPack.item.WeaponItem;

public class WeaponPanel extends EquipablePanel {

	private static final long serialVersionUID = 1L;

	private EffectAbilityEditor baseAbilityEditor = new EffectAbilityEditor(true, true);
	private EffectAbilityEditor specialAbilityEditor = new EffectAbilityEditor(true, true);

	public WeaponPanel() {
		EventManager.getInstance().register(baseAbilityEditor);
		EventManager.getInstance().register(specialAbilityEditor);

		baseAbilityEditor.setBorder(LayoutUtils.createGroupBorder("weaponEditor.baseAbility"));
		specialAbilityEditor.setBorder(LayoutUtils.createGroupBorder("equipableEditor.specialAbility"));
		finalizeLayout(LayoutUtils.createVerticalBox(baseAbilityEditor, specialAbilityEditor));

	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		super.bindTo(itemEditor);
		itemEditor.bind(baseAbilityEditor, WeaponItem::getBaseAbility, WeaponItem::setBaseAbility, WeaponItem.class);
		itemEditor.bind(specialAbilityEditor, WeaponItem::getSpecialAbility, WeaponItem::setSpecialAbility, WeaponItem.class);
	}
}
