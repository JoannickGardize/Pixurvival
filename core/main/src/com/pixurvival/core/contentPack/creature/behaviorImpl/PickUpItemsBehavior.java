package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntitySearchResult;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PickUpItemsBehavior extends Behavior {

	public static final float WORK_FINISHED_DISTANCE_TOLERANCE = 1;

	private static final long serialVersionUID = 1L;

	@Positive
	private float searchDistance = 10;

	@Valid
	private ElementSet<Item> items = new AllElementSet<>();

	@Override
	protected void step(CreatureEntity creature) {
		Entity currentItemTarget = (Entity) creature.getBehaviorData().getCustomData();
		if (currentItemTarget == null) {
			EntitySearchResult result = creature.findClosest(EntityGroup.ITEM_STACK, searchDistance, e -> items.contains(((ItemStackEntity) e).getItemStack().getItem()));
			if (result.getDistanceSquared() <= searchDistance * searchDistance) {
				currentItemTarget = result.getEntity();
				creature.getBehaviorData().setCustomData(currentItemTarget);
			} else {
				stop(creature);
				creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
				creature.getBehaviorData().setNothingToDo(true);
				return;
			}
		}
		if (currentItemTarget.isAlive()) {
			float distanceSquared = creature.distanceSquared(currentItemTarget);
			if (distanceSquared > WORK_FINISHED_DISTANCE_TOLERANCE * WORK_FINISHED_DISTANCE_TOLERANCE) {
				float distance = (float) Math.sqrt(distanceSquared);
				creature.moveTowardPrecisely(currentItemTarget, distance);
				creature.setTargetEntity(currentItemTarget);
				creature.getBehaviorData().setNextUpdateDelayRelativeToSpeed(distance);
			} else {
				creature.setForward(false);
				creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);

			}
		} else {
			stop(creature);
			creature.getBehaviorData().setNextUpdateDelayMillis(0);
			if (creature.distanceSquared(currentItemTarget) <= WORK_FINISHED_DISTANCE_TOLERANCE * WORK_FINISHED_DISTANCE_TOLERANCE) {
				creature.getBehaviorData().setTaskFinished(true);
			}
		}
	}

	public void stop(CreatureEntity creature) {
		creature.setForward(false);
		creature.getBehaviorData().setCustomData(null);
	}

}
