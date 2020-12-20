package com.pixurvival.contentPackEditor.component.item;

import com.pixurvival.contentPackEditor.component.abilitySet.AlterationAbilityEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.ItemStackEditor;
import com.pixurvival.core.livingEntity.ability.ItemAlterationAbility;

public class ItemAlterationAbilityEditor extends AlterationAbilityEditor<ItemAlterationAbility> {

	private static final long serialVersionUID = 1L;

	public ItemAlterationAbilityEditor(boolean useScrollPane) {
		super(ItemAlterationAbility.class);
		ItemStackEditor ammunitionEditor = new ItemStackEditor(false);
		bind(ammunitionEditor, "ammunition");
		build(useScrollPane, "alterationAbilityEditor.ammunition", ammunitionEditor);
	}
}
