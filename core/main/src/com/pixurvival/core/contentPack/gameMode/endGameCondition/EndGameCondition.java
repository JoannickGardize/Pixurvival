package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import java.io.Serializable;

import com.pixurvival.core.World;

import lombok.Getter;
import lombok.Setter;

public abstract class EndGameCondition implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient @Getter @Setter int id;

	public void initialize(World world) {

	}

	public void initializeNewGameData(World world) {

	}

	/**
	 * @param world
	 * @return true if the game is over
	 */
	public abstract boolean update(World world);

	protected Object getGameData(World world) {
		return world.getEndGameConditionData().get(id);
	}

	protected void setGameData(World world, Object value) {
		world.getEndGameConditionData().put(id, value);
	}
}
