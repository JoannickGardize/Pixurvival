package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.WorldRandom;

public class WanderBehavior extends Behavior {

	public static final double MOVE_PROBABILITY = 0.5;
	public static final double MEAN_TIME = 800;
	public static final double DEVIATON_TIME = 500;

	private static final long serialVersionUID = 1L;

	@Override
	protected void step(CreatureEntity creature) {
		WorldRandom random = creature.getWorld().getRandom();
		if (random.nextDouble() < MOVE_PROBABILITY) {
			creature.move(random.nextAngle());
		} else {
			creature.setForward(false);
		}
		creature.getBehaviorData().setNextUpdateDelayMillis((long) (MEAN_TIME + random.nextGaussian() * DEVIATON_TIME));
	}
}
