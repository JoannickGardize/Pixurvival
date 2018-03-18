package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.contentPack.sprite.ActionAnimation;

public class Activity {

	public static Activity NONE = new Activity();

	public boolean canMove() {
		return true;
	}

	/**
	 * @return The animation to play, or null if no special animation is required.
	 */
	public ActionAnimation getActionAnimation() {
		return null;
	}

	public void update() {
	}

}
