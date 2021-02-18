package com.pixurvival.core.system;

import com.pixurvival.core.World;

public interface GameSystem {

	/**
	 * Called before initializing this system to check if it is required for the
	 * given game mode. If not, this system will be removed from the world. True by
	 * default. Note that attributes are not injected at this point.
	 * 
	 * @param world
	 *            the starting world
	 * @return true if this system is required for the starting world run, false
	 *         otherwise.
	 */
	default boolean isRequired(World world) {
		return true;
	}

	/**
	 * Called right after attributes injection.
	 * 
	 * @param world
	 */
	default void initialize(World world) {
		// Nothing by default.
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
