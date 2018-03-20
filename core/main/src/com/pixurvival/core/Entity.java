package com.pixurvival.core;

import java.nio.ByteBuffer;

import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.util.Collisions;
import com.pixurvival.core.util.Vector2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Entity implements Collidable {

	private @Setter long id;
	private @Setter(AccessLevel.PACKAGE) World world;
	private Vector2 position = new Vector2();
	private @Setter boolean alive = true;
	private @Setter Object customData;

	private double speed;
	private @Setter double movingAngle;
	private @Setter boolean forward;
	private Vector2 velocity = new Vector2();

	public abstract void initialize();

	public void update() {
		// Update position
		if (forward) {
			speed = getSpeedPotential();
			velocity.x = Math.cos(movingAngle) * speed;
			velocity.y = Math.sin(movingAngle) * speed;
			double dx = velocity.x * getWorld().getTime().getDeltaTime();
			double dy = velocity.y * getWorld().getTime().getDeltaTime();
			if (isSolid() && getWorld().getMap().collide(this, dx, 0)) {
				if (velocity.x > 0) {
					getPosition().x = Math.floor(getPosition().x) + 1 - getBoundingRadius();
				} else {
					getPosition().x = Math.floor(getPosition().x) + getBoundingRadius();
				}
				velocity.x = 0;
			} else {
				getPosition().x += dx;
			}
			if (isSolid() && getWorld().getMap().collide(this, 0, dy)) {
				if (velocity.y > 0) {
					getPosition().y = Math.floor(getPosition().y) + 1 - getBoundingRadius();
				} else {
					getPosition().y = Math.floor(getPosition().y) + getBoundingRadius();
				}
				velocity.y = 0;
			} else {
				getPosition().y += dy;
			}
		} else {
			velocity.set(0, 0);
		}
	}

	public abstract EntityGroup getGroup();

	public abstract double getBoundingRadius();

	public abstract void writeUpdate(ByteBuffer buffer);

	public abstract void applyUpdate(ByteBuffer buffer);

	public abstract double getSpeedPotential();

	public abstract boolean isSolid();

	@Override
	public double getX() {
		return position.getX();
	}

	@Override
	public double getY() {
		return position.getY();
	}

	@Override
	public double getHalfWidth() {
		return getBoundingRadius();
	}

	@Override
	public double getHalfHeight() {
		return getBoundingRadius();
	}

	public double distanceSquared(Entity other) {
		return position.distanceSquared(other.position);
	}

	public double angleTo(Entity other) {
		return position.angleTo(other.position);
	}

	public double angleTo(MapStructure structure) {
		return position.angleTo(new Vector2(structure.getX(), structure.getY()));
	}

	public boolean collide(Entity other) {
		return Collisions.circleCircle(position, getBoundingRadius(), other.position, other.getBoundingRadius());
	}

	public boolean collideDynamic(Entity other) {
		return Collisions.dynamicCircleCircle(position, getBoundingRadius(),
				velocity.copy().mul(world.getTime().getDeltaTime()), other.position, other.getBoundingRadius());
	}
}
