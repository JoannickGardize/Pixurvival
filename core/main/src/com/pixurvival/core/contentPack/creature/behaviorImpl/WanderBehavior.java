package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.WorldRandom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WanderBehavior extends Behavior {

	public static final double MEAN_TIME = 700;
	public static final double DEVIATON_TIME = 500;
	public static final double MIN_TIME = 200;
	public static final double MAX_TIME = 1200;
	private static final double MAX_ANCHOR_DISTANCE = 10;

	private static final long serialVersionUID = 1L;

	private boolean anchor = true;
	private double moveRate = 0.3;
	private double forwardFactor = 1;

	@Override
	protected void step(CreatureEntity creature) {
		WorldRandom random = creature.getWorld().getRandom();
		if (anchor && creature.getPosition().distanceSquared(creature.getAnchorPosition()) >= MAX_ANCHOR_DISTANCE * MAX_ANCHOR_DISTANCE) {
			creature.move(creature.getPosition().angleToward(creature.getAnchorPosition()));
		} else {
			if (random.nextDouble() < moveRate) {

				creature.move(random.nextAngle(), forwardFactor);
			} else {
				creature.setForward(false);
			}
		}
		creature.getBehaviorData().setNextUpdateDelayMillis((long) (MathUtils.clamp(MEAN_TIME + random.nextGaussian() * DEVIATON_TIME, MIN_TIME, MAX_TIME)));
	}
}
