package fr.sharkhendrix.pixurvival.core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AliveEntity extends Entity implements Damageable {

	private double health = getMaxHealth();
	private double movingAngle;
	private boolean forward;

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
			double ds = getSpeedPotential() * getWorld().getTime().getDeltaTime();
			double dx = Math.cos(movingAngle) * ds;
			double dy = Math.sin(movingAngle) * ds;
			if (isSolid() && getWorld().getMap().collide(this, dx, 0)) {
				getPosition().x = (int) (1 + getPosition().x - getBoundingRadius())
						- (dx > 0 ? getBoundingRadius() * 2 : 0);

			} else {
				getPosition().x += dx;
			}
			if (isSolid() && getWorld().getMap().collide(this, 0, dy)) {
				getPosition().y = (int) (1 + getPosition().y - getBoundingRadius())
						- (dy > 0 ? getBoundingRadius() * 2 : 0);
			} else {
				getPosition().y += dy;
			}
		}

		// Only server has the final decision to kill an alive entity
		if (getWorld().isServer() && health <= 0) {
			setAlive(false);
		}
	}

	public abstract double getSpeedPotential();

	public abstract boolean isSolid();
}
