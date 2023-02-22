package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.Positionnable;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.livingEntity.CreatureEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveTowardStructureBehavior extends AbstractMoveTowardBehavior {

    private static final long serialVersionUID = 1L;

    @Valid
    private ElementSet<Structure> structureSet = new AllElementSet<>();

    @Override
    protected Positionnable findTarget(CreatureEntity creature) {
        return creature.getWorld().getMap().findClosestStructure(creature.getPosition(), BehaviorData.TARGET_SEARCH_RADIUS, structureSet);
    }

}
