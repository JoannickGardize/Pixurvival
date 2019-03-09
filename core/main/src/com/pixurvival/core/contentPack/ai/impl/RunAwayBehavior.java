package com.pixurvival.core.contentPack.ai.impl;

import com.pixurvival.core.Entity;
import com.pixurvival.core.aliveEntity.creature.CreatureEntity;
import com.pixurvival.core.contentPack.ai.Behavior;

public class RunAwayBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	@Override
	protected void step(CreatureEntity creature) {
		Entity player = creature.getBehaviorData().getClosestPlayer();
		if (player == null) {
			creature.setForward(false);
		} else {
			creature.setMovingAngle(player.angleTo(creature));
			creature.setForward(true);
		}
	}

	@Override
	protected void end(CreatureEntity creature) {
	}

}
