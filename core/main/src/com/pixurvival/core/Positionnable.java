package com.pixurvival.core;

import com.pixurvival.core.util.Vector2;

public interface Positionnable {

	Vector2 getPosition();

	World getWorld();

	default float distanceSquared(Positionnable other) {
		return getPosition().distanceSquared(other.getPosition());
	}
}
