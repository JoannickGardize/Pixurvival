package com.pixurvival.core.contentPack.creature.behaviorImpl;

import java.util.function.Function;

import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.Vector2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WanderAnchor {
	NONE(c -> null),
	SPAWN_POSITION(c -> c.getSpawnPosition()),
	MASTER(c -> c.getMaster() == null ? null : c.getMaster().getPosition());

	Function<CreatureEntity, Vector2> anchorGetter;
}
