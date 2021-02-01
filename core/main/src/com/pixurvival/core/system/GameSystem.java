package com.pixurvival.core.system;

import com.pixurvival.core.contentPack.gameMode.GameMode;
import com.pixurvival.core.system.interest.Interest;

public interface GameSystem extends Interest {

	/**
	 * Called before initializing this system to check if it is required for the
	 * given game mode. If not, this system will be removed from the world. True by
	 * default.
	 * 
	 * @param gameMode
	 *            the game mode used by the starting world run
	 * @return true if this system is required for the starting world run, false
	 *         otherwise.
	 */
	default boolean isRequired(GameMode gameMode) {
		return true;
	}

	/**
	 * Called when a {@link SystemData} for this system has been received from an
	 * authority server.
	 * 
	 * @param data
	 */
	default void accept(SystemData data) {
		// No data by default
	}
}
