package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.Damageable;
import com.pixurvival.core.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AliveEntity extends Entity implements Damageable {

	private Activity activity;
	private double health = getMaxHealth();
	private double aimingAngle;

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
		super.update();

		// Only server has the final decision to kill an alive entity
		if (getWorld().isServer() && health <= 0) {
			setAlive(false);
		}
	}
}