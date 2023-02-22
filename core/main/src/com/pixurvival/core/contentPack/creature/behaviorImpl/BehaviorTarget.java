package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@AllArgsConstructor
public enum BehaviorTarget {
    CLOSEST_ENNEMY(c -> c.getBehaviorData().getClosestEnnemy()),
    MASTER(c -> c.getMaster() instanceof Entity ? (Entity) c.getMaster() : null),
    SELF(c -> c),
    NONE(c -> null);

    private @Getter Function<CreatureEntity, Entity> entityGetter;
}
