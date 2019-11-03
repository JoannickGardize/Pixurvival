package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.abilitySet.ItemAlterationAbilityEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.contentPack.item.WeaponItem;

public class WeaponPanel extends EquipablePanel {

	private static final long serialVersionUID = 1L;

	private ItemAlterationAbilityEditor baseAbilityEditor = new ItemAlterationAbilityEditor(true);
	private ItemAlterationAbilityEditor specialAbilityEditor = new ItemAlterationAbilityEditor(true);

	public WeaponPanel() {
		super(true);
		baseAbilityEditor.setBorder(LayoutUtils.createGroupBorder("weaponEditor.baseAbility"));
		specialAbilityEditor.setBorder(LayoutUtils.createGroupBorder("equipableEditor.specialAbility"));
		finalizeLayout(LayoutUtils.createVerticalBox(baseAbilityEditor, specialAbilityEditor));

	}

	@Override
	public void bindTo(ItemEditor itemEditor) {
		bindTo(itemEditor, WeaponItem.class);
		itemEditor.bind(baseAbilityEditor, WeaponItem::getBaseAbility, WeaponItem::setBaseAbility, WeaponItem.class);
		itemEditor.bind(specialAbilityEditor, WeaponItem::getSpecialAbility, WeaponItem::setSpecialAbility, WeaponItem.class);
	}
}
