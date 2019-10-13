package com.pixurvival.contentPackEditor.component.abilitySet;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ListEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.RootElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.VerticalListEditor;
import com.pixurvival.core.livingEntity.ability.AbilitySet;
import com.pixurvival.core.livingEntity.ability.AlterationAbility;

public class AbilitySetEditor extends RootElementEditor<AbilitySet<AlterationAbility>> {

	private static final long serialVersionUID = 1L;

	private ListEditor<AlterationAbility> abilitiesEditor = new VerticalListEditor<>(() -> new AlterationAbilityEditor(false, false), AlterationAbility::new, VerticalListEditor.HORIZONTAL);

	public AbilitySetEditor() {
		bind(abilitiesEditor, AbilitySet::getAbilities, AbilitySet::setAbilities);

		LayoutUtils.fill(this, abilitiesEditor);
	}
}
