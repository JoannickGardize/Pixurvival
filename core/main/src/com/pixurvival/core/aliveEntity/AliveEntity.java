package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.Entity;
import com.pixurvival.core.aliveEntity.ability.AbilityData;
import com.pixurvival.core.aliveEntity.ability.AbilitySet;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AliveEntity<T extends AliveEntity<T>> extends Entity implements Damageable {

	private double health;
	private double aimingAngle;

	private AbilityData[] abilityData;

	@Override
	public void initialize() {
		health = getMaxHealth();
		AbilitySet<T> abilitySet = getAbilitySet();
		abilityData = new AbilityData[abilitySet.size()];
		for (int i = 0; i < abilitySet.size(); i++) {
			abilityData[i] = abilitySet.get(i).getAbilityData(this);
		}
	}

	@Override
	public void takeDamage(double amount) {
		health -= amount;
		if (health < 0) {
			health = 0;
		}
	}

	@Override
	public void takeHeal(double amount) {
		health += amount;
		if (health > getMaxHealth()) {
			health = getMaxHealth();
		}
	}

	@Override
	public void update() {
		// Only server has the final decision to kill an alive entity
		if (getWorld().isServer() && health <= 0) {
			setAlive(false);
		}

		super.update();
	}

	public AbilityData getAbilityData(int abilityId) {
		return abilityData[abilityId];
	}

	public abstract AbilitySet<T> getAbilitySet();
}
