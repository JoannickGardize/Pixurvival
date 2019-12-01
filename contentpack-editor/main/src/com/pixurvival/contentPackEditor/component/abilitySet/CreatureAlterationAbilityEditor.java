package com.pixurvival.contentPackEditor.component.abilitySet;

import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.livingEntity.ability.CreatureAlterationAbility;

public class CreatureAlterationAbilityEditor extends AlterationAbilityEditor<CreatureAlterationAbility> {

	private static final long serialVersionUID = 1L;

	public CreatureAlterationAbilityEditor() {
		FloatInput predictionBulletSpeedInput = new FloatInput(Bounds.positive());
		bind(predictionBulletSpeedInput, CreatureAlterationAbility::getPredictionBulletSpeed, CreatureAlterationAbility::setPredictionBulletSpeed);
		build(false, "alterationAbilityEditor.predictionBulletSpeed", predictionBulletSpeedInput);
	}
}
