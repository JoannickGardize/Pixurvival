package com.pixurvival.core.aliveEntity.ability;

import com.pixurvival.core.contentPack.sprite.ActionAnimation;

public class Activity {

	public static final byte NONE_ID = 0;
	public static final byte HARVESTING_ID = 1;
	public static final byte CRAFTING_ACTIVITY_ID = 2;

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

	public byte getId() {
		return NONE_ID;
	}

	public boolean in(byte... activityIds) {
		for (int i = 0; i < activityIds.length; i++) {
			if (getId() == activityIds[i]) {
				return true;
			}
		}
		return false;
	}
}
