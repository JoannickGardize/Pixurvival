package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.abilitySet.EffectAbilityEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.item.WeaponItem;

public class WeaponEditor extends EquipableEditor<WeaponItem> {

	private static final long serialVersionUID = 1L;

	public WeaponEditor() {

		// Construction

		EffectAbilityEditor baseAbilityEditor = new EffectAbilityEditor(true, true);
		EventManager.getInstance().register(baseAbilityEditor);
		EffectAbilityEditor specialAbilityEditor = new EffectAbilityEditor(true, true);
		EventManager.getInstance().register(specialAbilityEditor);

		// Binding

		bind(baseAbilityEditor, WeaponItem::getBaseAbility, WeaponItem::setBaseAbility);
		bind(specialAbilityEditor, WeaponItem::getSpecialAbility, WeaponItem::setSpecialAbility);

		// Layouting

		baseAbilityEditor.setBorder(LayoutUtils.createGroupBorder("weaponEditor.baseAbility"));
		specialAbilityEditor.setBorder(LayoutUtils.createGroupBorder("equipableEditor.specialAbility"));
		LayoutUtils.addVertically(getRightPanel(), baseAbilityEditor, specialAbilityEditor);
		finalizeLayouting();
	}
}
