package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.FloatComparison;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistanceToStructureCondition extends ChangeCondition {

    private static final long serialVersionUID = 1L;

    @Valid
    private ElementSet<Structure> structureSet = new AllElementSet<>();

    private FloatComparison operator;

    @Positive
    private float targetDistance;

    @Override
    public boolean test(CreatureEntity creature) {
        return operator.testPresence(creature.getWorld().getMap().findClosestStructure(creature.getPosition(), targetDistance, structureSet));
    }

}
