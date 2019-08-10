package com.pixurvival.core.contentPack.creature.behaviorImpl;

import java.util.function.Function;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BehaviorTarget {
	CLOSEST_ENNEMY(c -> c.getBehaviorData().getClosestEnnemy()),
	MASTER(c -> c.getMaster() instanceof Entity ? (Entity) c.getMaster() : null),
	SELF(c -> c);

	private @Getter Function<CreatureEntity, Entity> entityGetter;
}
