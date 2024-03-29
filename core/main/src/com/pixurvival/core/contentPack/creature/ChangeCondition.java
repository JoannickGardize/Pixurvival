package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.validation.annotation.ElementReference;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.livingEntity.CreatureEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ChangeCondition extends IdentifiedElement {

    private static final long serialVersionUID = 1L;

    @ElementReference("<<<")
    private Behavior nextBehavior;

    @Positive
    private float affectedNeighborsDistance;

    public abstract boolean test(CreatureEntity creature);
}
