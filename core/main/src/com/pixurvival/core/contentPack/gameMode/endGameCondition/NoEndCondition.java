package com.pixurvival.core.contentPack.gameMode.endGameCondition;

import com.pixurvival.core.World;

public class NoEndCondition implements EndGameCondition {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean update(World world) {
		return false;
	}

}
