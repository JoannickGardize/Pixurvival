package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.livingEntity.CreatureEntity;

public class NothingToDoCondition extends ChangeCondition {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean test(CreatureEntity creature) {
        return creature.getBehaviorData().isNothingToDo();
    }

}
