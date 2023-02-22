package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.CreatureEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeCondition extends ChangeCondition {

    private static final long serialVersionUID = 1L;

    @Positive
    private long targetTime;

    @Override
    public boolean test(CreatureEntity creature) {
        return creature.getBehaviorData().getElapsedTimeMillis() >= targetTime;
    }
}
