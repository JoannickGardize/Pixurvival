package com.pixurvival.core;

import java.nio.ByteBuffer;

import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.util.Collisions;
import com.pixurvival.core.util.Vector2;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class Entity implements Collidable, CustomDataHolder {

	private @Setter long id;
	private @Setter(AccessLevel.PACKAGE) World world;
	private Vector2 position = new Vector2();
	private @Setter boolean alive = true;
	private @Setter Object customData;

	private double speed = 0;
	private double movingAngle = 0;
	private boolean forward = false;
	private double forwardFactor = 1;
	private @Getter(AccessLevel.NONE) Vector2 targetVelocity = new Vector2();
	private Vector2 velocity = new Vector2();
	private boolean velocityChanged = false;

	public abstract void initialize();

	public void setMovingAngle(double movingAngle) {
		if (this.movingAngle != movingAngle) {
			this.movingAngle = movingAngle;
			velocityChanged = true;
		}
	}

	public void setForward(boolean forward) {
		if (this.forward != forward) {
			this.forward = forward;
			velocityChanged = true;
		}
	}

	public void setSpeed(double speed) {
		if (this.speed != speed) {
			this.speed = speed;
			velocityChanged = true;
		}
	}

	public void update() {
		// Update position
		if (forward) {
			setSpeed(getSpeedPotential() * forwardFactor);
			updateVelocity();
			double dx = targetVelocity.getX() * getWorld().getTime().getDeltaTime();
			double dy = targetVelocity.getY() * getWorld().getTime().getDeltaTime();
			if (isSolid() && getWorld().getMap().collide(this, dx, 0)) {
				if (targetVelocity.getX() > 0) {
					getPosition().setX(Math.floor(getPosition().getX()) + 1 - getBoundingRadius());
				} else {
					getPosition().setX(Math.floor(getPosition().getX()) + getBoundingRadius());
				}
				velocity.setX(0);
			} else {
				getPosition().addX(dx);
				velocity.setX(targetVelocity.getX());
			}
			if (isSolid() && getWorld().getMap().collide(this, 0, dy)) {
				if (targetVelocity.getY() > 0) {
					getPosition().setY(Math.floor(getPosition().getY()) + 1 - getBoundingRadius());
				} else {
					getPosition().setY(Math.floor(getPosition().getY()) + getBoundingRadius());
				}
				velocity.setY(0);
			} else {
				getPosition().addY(dy);
				velocity.setY(targetVelocity.getY());
			}
		} else {
			setSpeed(0);
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

	public double distanceSquared(Vector2 position) {
		return this.position.distanceSquared(position);
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
		return Collisions.dynamicCircleCircle(position, getBoundingRadius(), targetVelocity.copy().mul(world.getTime().getDeltaTime()), other.position, other.getBoundingRadius());
	}

	private void updateVelocity() {
		if (velocityChanged) {
			targetVelocity.setFromEuclidean(speed, movingAngle);
			velocityChanged = false;
		}
	}
}
