package com.pixurvival.core.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

import com.pixurvival.core.Positionnable;
import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.team.TeamMember;
import com.pixurvival.core.util.Vector2;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EntitySearchUtils {

	/**
	 * @param group
	 * @param maxSquareDistance
	 *            The maximum distance between this entity and any point of checked
	 *            chunks.
	 * @param action
	 */
	public static void foreachEntities(Positionnable origin, EntityGroup group, float maxSquareDistance, Consumer<Entity> action) {
		TiledMap map = origin.getWorld().getMap();
		map.forEachChunk(origin.getPosition(), maxSquareDistance, c -> c.getEntities().get(group).forEach(action::accept));
	}

	public static void forEach(TeamMember searcher, TargetType targetType, float maxSquareDistance, Consumer<LivingEntity> action) {
		Consumer<Entity> actionFilter = e -> {
			LivingEntity livingEntity = (LivingEntity) e;
			if (targetType.getTest().test(searcher, livingEntity)) {
				action.accept(livingEntity);
			}
		};
		foreachEntities(searcher, EntityGroup.PLAYER, maxSquareDistance, actionFilter);
		foreachEntities(searcher, EntityGroup.CREATURE, maxSquareDistance, actionFilter);
	}

	public static EntitySearchResult findClosest(TeamMember searcher, TargetType targetType, float maxSquareDistance) {
		EntitySearchResult searchResult = new EntitySearchResult();
		forEach(searcher, targetType, maxSquareDistance, e -> {
			float distance = searcher.getPosition().distanceSquared(e.getPosition());
			if (distance < searchResult.getDistanceSquared()) {
				searchResult.setDistanceSquared(distance);
				searchResult.setEntity(e);
			}
		});
		return searchResult;
	}

	public static Vector2 closest(Vector2 position, Collection<? extends Positionnable> elements) {
		Iterator<? extends Positionnable> iterator = elements.iterator();
		Vector2 closest = iterator.next().getPosition();
		float closestDistance = closest.distanceSquared(position);
		while (iterator.hasNext()) {
			Vector2 current = iterator.next().getPosition();
			float distance = current.distanceSquared(position);
			if (distance < closestDistance) {
				closest = current;
				closestDistance = distance;
			}
		}
		return closest;
	}
}
