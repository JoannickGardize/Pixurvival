package com.pixurvival.core.entity;

import java.util.function.Consumer;

import lombok.experimental.UtilityClass;

import com.pixurvival.core.Positionnable;
import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.map.chunk.Chunk;
import com.pixurvival.core.team.TeamMember;

@UtilityClass
public class EntitySearchUtils {

	/**
	 * @param group
	 * @param maxSquareDistance
	 *            The maximum distance between this entity and any point of
	 *            checked chunks.
	 * @param action
	 */
	public static void foreachEntities(Positionnable origin, EntityGroup group, double maxSquareDistance, Consumer<Entity> action) {
		TiledMap map = origin.getWorld().getMap();
		map.forEachChunk(origin.getPosition(), maxSquareDistance, (Consumer<Chunk>) c -> c.getEntities().get(group).forEach(action::accept));
	}

	public static void forEach(TeamMember searcher, TargetType targetType, double maxSquareDistance, Consumer<LivingEntity> action) {
		Consumer<Entity> actionFilter = e -> {
			LivingEntity livingEntity = (LivingEntity) e;
			if (targetType.getTest().test(searcher, livingEntity)) {
				action.accept(livingEntity);
			}
		};
		foreachEntities(searcher, EntityGroup.PLAYER, maxSquareDistance, actionFilter);
		foreachEntities(searcher, EntityGroup.CREATURE, maxSquareDistance, actionFilter);
	}

	public static EntitySearchResult findClosest(TeamMember searcher, TargetType targetType, double maxSquareDistance) {
		EntitySearchResult searchResult = new EntitySearchResult();
		forEach(searcher, targetType, maxSquareDistance, e -> {
			double distance = searcher.getPosition().distanceSquared(e.getPosition());
			if (distance < searchResult.getDistanceSquared()) {
				searchResult.setDistanceSquared(distance);
				searchResult.setEntity(e);
			}
		});
		return searchResult;
	}
}
