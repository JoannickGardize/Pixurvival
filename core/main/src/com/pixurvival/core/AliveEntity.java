package com.pixurvival.core;

import com.pixurvival.core.util.Vector2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AliveEntity extends Entity implements Damageable {

	private double health = getMaxHealth();
	private double movingAngle;
	private double speed;
	private double aimingAngle;
	private boolean forward;
	private Vector2 velocity = new Vector2();

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

		// Update position
		if (forward) {
			speed = getSpeedPotential() * getWorld().getMap().tileAt((int) getPosition().x, (int) getPosition().y)
					.getTileDefinition().getVelocityFactor();
			velocity.x = Math.cos(movingAngle) * speed;
			velocity.y = Math.sin(movingAngle) * speed;
			double dx = velocity.x * getWorld().getTime().getDeltaTime();
			double dy = velocity.y * getWorld().getTime().getDeltaTime();
			if (isSolid() && getWorld().getMap().collide(this, dx, 0)) {
				if (velocity.x > 0) {
					getPosition().x = (((long) getPosition().x) + 1) - getBoundingRadius();
				} else {
					getPosition().x = ((long) getPosition().x) + getBoundingRadius();
				}
				velocity.x = 0;
			} else {
				getPosition().x += dx;
			}
			if (isSolid() && getWorld().getMap().collide(this, 0, dy)) {
				if (velocity.y > 0) {
					getPosition().y = (((long) getPosition().y) + 1) - getBoundingRadius();
				} else {
					getPosition().y = ((long) getPosition().y) + getBoundingRadius();
				}
				velocity.y = 0;
			} else {
				getPosition().y += dy;
			}
		} else {
			velocity.set(0, 0);
		}

		// Only server has the final decision to kill an alive entity
		if (getWorld().isServer() && health <= 0) {
			setAlive(false);
		}
	}

	public abstract double getSpeedPotential();

	public abstract boolean isSolid();
}
