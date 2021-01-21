package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.validation.annotation.Positive;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatureAlterationAbility extends AlterationAbility {

	private static final long serialVersionUID = 1L;

	@Positive
	private float predictionBulletSpeed;

	@Override
	public Ability copy() {
		CreatureAlterationAbility ability = new CreatureAlterationAbility();
		ability.setAlterations(getAlterations());
		ability.setCooldown(getCooldown());
		ability.setPredictionBulletSpeed(predictionBulletSpeed);
		return ability;
	}
}
