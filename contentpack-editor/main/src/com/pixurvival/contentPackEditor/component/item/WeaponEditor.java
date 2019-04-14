package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.core.item.Item.Weapon;

public class WeaponEditor extends EquipableEditor<Weapon> {

	private static final long serialVersionUID = 1L;

	public WeaponEditor() {

		// Construction

		EffectAbilityEditor baseAbilityEditor = new EffectAbilityEditor();
		EffectAbilityEditor specialAbilityEditor = new EffectAbilityEditor();

		// Binding

		bind(baseAbilityEditor, Weapon::getBaseAbility, Weapon::setBaseAbility);
		bind(specialAbilityEditor, Weapon::getSpecialAbility, Weapon::setSpecialAbility);

		// Layouting

		baseAbilityEditor.setBorder(LayoutUtils.createGroupBorder("weaponEditor.baseAbility"));
		specialAbilityEditor.setBorder(LayoutUtils.createGroupBorder("equipableEditor.specialAbility"));
		LayoutUtils.addVertically(getRightPanel(), baseAbilityEditor, specialAbilityEditor);
		finalizeLayouting();
	}
}
