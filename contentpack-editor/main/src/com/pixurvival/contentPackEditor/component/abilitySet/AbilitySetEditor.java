package com.pixurvival.contentPackEditor.component.abilitySet;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.livingEntity.ability.Ability;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.CreatureAlterationAbility;

public class AbilitySetEditor extends RootElementEditor<AbilitySet> {

	private static final long serialVersionUID = 1L;

	public AbilitySetEditor() {
		ListEditor<Ability> abilitiesEditor = new VerticalListEditor<>(CreatureAlterationAbilityEditor::new, CreatureAlterationAbility::new, VerticalListEditor.VERTICAL);
		bind(abilitiesEditor, AbilitySet::getAbilities, AbilitySet::setAbilities);

		LayoutUtils.fill(this, abilitiesEditor);
	}
}
