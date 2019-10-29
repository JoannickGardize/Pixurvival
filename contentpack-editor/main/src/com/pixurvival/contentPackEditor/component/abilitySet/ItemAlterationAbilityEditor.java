package com.pixurvival.contentPackEditor.component.abilitySet;

import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;

public class ItemAlterationAbilityEditor extends AlterationAbilityEditor<ItemAlterationAbility> {

	private static final long serialVersionUID = 1L;

	public ItemAlterationAbilityEditor(boolean useScrollPane) {
		ItemStackEditor ammunitionEditor = new ItemStackEditor(false);
		bind(ammunitionEditor, ItemAlterationAbility::getAmmunition, ItemAlterationAbility::setAmmunition);
		build(useScrollPane, "alterationAbilityEditor.ammunition", ammunitionEditor);
	}
}
