package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.PseudoAIUtils;

// TODO ???
public class ClearWayCondition extends ChangeCondition {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean test(CreatureEntity creature) {
        if (creature.getTargetEntity() == null) {
            return false;
        }
        return !PseudoAIUtils.collideInDirection(creature, creature.angleToward(creature.getTargetEntity()), (int) CreatureEntity.DEFAULT_OBSTACLE_VISION_DISTANCE);
    }

}
