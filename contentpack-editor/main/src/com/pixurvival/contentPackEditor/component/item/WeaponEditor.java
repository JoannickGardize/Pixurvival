package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.abilitySet.EffectAbilityEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.event.EventManager;
import com.pixurvival.core.item.Weapon;

public class WeaponEditor extends EquipableEditor<Weapon> {

	private static final long serialVersionUID = 1L;

	public WeaponEditor() {

		// Construction

		EffectAbilityEditor baseAbilityEditor = new EffectAbilityEditor(true, true);
		EventManager.getInstance().register(baseAbilityEditor);
		EffectAbilityEditor specialAbilityEditor = new EffectAbilityEditor(true, true);
		EventManager.getInstance().register(specialAbilityEditor);

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
