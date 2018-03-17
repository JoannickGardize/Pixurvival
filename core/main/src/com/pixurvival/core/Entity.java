package com.pixurvival.core;

import java.nio.ByteBuffer;

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

	public abstract void initialize();

	public abstract void update();

	public abstract EntityGroup getGroup();

	public abstract double getBoundingRadius();

	public abstract void writeUpdate(ByteBuffer buffer);

	public abstract void applyUpdate(ByteBuffer buffer);

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

}
