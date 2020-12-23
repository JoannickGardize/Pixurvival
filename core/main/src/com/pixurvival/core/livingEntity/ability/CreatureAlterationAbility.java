package com.pixurvival.core.livingEntity.ability;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatureAlterationAbility extends AlterationAbility {

	private static final long serialVersionUID = 1L;

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
