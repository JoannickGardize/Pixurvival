package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.core.util.WorldRandom;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WanderBehavior extends Behavior {

    public static final float MEAN_TIME = 700;
    public static final float DEVIATON_TIME = 500;
    public static final float MIN_TIME = 300;
    public static final float MAX_TIME = 800;
    private static final float MAX_ANCHOR_DISTANCE = 10;

    private static final long serialVersionUID = 1L;

    private WanderAnchor anchorType;
    private float moveRate = 0.3f;
    private float forwardFactor = 1;

    @Override
    protected void step(CreatureEntity creature) {
        WorldRandom random = creature.getWorld().getRandom();
        Vector2 anchorPosition = anchorType.getAnchorGetter().apply(creature);
        if (anchorPosition != null && creature.getPosition().distanceSquared(anchorPosition) >= MAX_ANCHOR_DISTANCE * MAX_ANCHOR_DISTANCE) {
            creature.move(creature.getPosition().angleToward(anchorPosition), forwardFactor);
        } else {
            if (random.nextFloat() < moveRate) {
                creature.move(random.nextAngle(), forwardFactor);
            } else {
                creature.setForward(false);
            }
        }
        creature.getBehaviorData().setNextUpdateDelayMillis((long) (MathUtils.clamp(MEAN_TIME + (float) random.nextGaussian() * DEVIATON_TIME, MIN_TIME, MAX_TIME)));
    }
}
