package com.pixurvival.contentPackEditor.component.abilitySet;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.FloatInput;
import com.pixurvival.core.livingEntity.ability.CreatureAlterationAbility;

public class CreatureAlterationAbilityEditor extends AlterationAbilityEditor<CreatureAlterationAbility> {

	private static final long serialVersionUID = 1L;

	public CreatureAlterationAbilityEditor() {
		super(CreatureAlterationAbility.class);
		FloatInput predictionBulletSpeedInput = new FloatInput();
		bind(predictionBulletSpeedInput, "predictionBulletSpeed");
		build(false, LayoutUtils.labelled("alterationAbilityEditor.predictionBulletSpeed", LayoutUtils.single(predictionBulletSpeedInput)));
	}
}
