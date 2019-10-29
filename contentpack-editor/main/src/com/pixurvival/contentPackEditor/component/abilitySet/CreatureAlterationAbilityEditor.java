package com.pixurvival.contentPackEditor.component.abilitySet;

import com.pixurvival.contentPackEditor.component.valueComponent.Bounds;
import com.pixurvival.contentPackEditor.component.valueComponent.DoubleInput;
import com.pixurvival.core.livingEntity.ability.CreatureAlterationAbility;

public class CreatureAlterationAbilityEditor extends AlterationAbilityEditor<CreatureAlterationAbility> {

	private static final long serialVersionUID = 1L;

	public CreatureAlterationAbilityEditor() {
		DoubleInput predictionBulletSpeedInput = new DoubleInput(Bounds.positive());
		bind(predictionBulletSpeedInput, CreatureAlterationAbility::getPredictionBulletSpeed, CreatureAlterationAbility::setPredictionBulletSpeed);
		build(false, "alterationAbilityEditor.predictionBulletSpeed", predictionBulletSpeedInput);
	}
}
