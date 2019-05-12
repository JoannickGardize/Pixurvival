package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import java.io.Serializable;

import com.pixurvival.core.World;

public interface EndGameCondition extends Serializable {

	default void initialize(World world) {
	}

	/**
	 * @param world
	 * @return true if the game is over
	 */
	boolean update(World world);
}
