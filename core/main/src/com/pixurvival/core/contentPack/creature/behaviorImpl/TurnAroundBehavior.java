package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TurnAroundBehavior extends Behavior {

    private static final long serialVersionUID = 1L;

    private BehaviorTarget targetType;

    @Positive
    private float minDistance;

    @Positive
    private float maxDistance;

    @Override
    public void begin(CreatureEntity creature) {
        super.begin(creature);
        Entity target = targetType.getEntityGetter().apply(creature);
        creature.setTargetEntity(target);
    }

    @Override
    protected void step(CreatureEntity creature) {
        Entity target = targetType.getEntityGetter().apply(creature);
        if (target == null) {
            creature.setForward(false);
            creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
            creature.getBehaviorData().setNothingToDo(true);
        } else {
            float distanceSquared = creature.distanceSquared(target);
            if (distanceSquared > maxDistance * maxDistance) {
                creature.moveToward(target);
            } else if (distanceSquared < minDistance * minDistance) {
                creature.getAwayFrom(target);
            } else {
                float aroundAngle = creature.getWorld().getRandom().nextBoolean() ? (float) Math.PI / 2 : -(float) Math.PI / 2;
                creature.move(creature.angleToward(target) + aroundAngle);
            }
            creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed();
        }
        creature.setTargetEntity(target);
    }

}
