package com.pixurvival.core.contentPack.ai;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.Time;
import com.pixurvival.core.aliveEntity.creature.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

public class BehaviorData {

	private Time time;

	@Getter
	@Setter
	private double beginTime;

	private @Getter CreatureEntity creature;

	private Entity closestPlayer;
	private @Getter double closestDistanceSquaredToPlayer;

	public BehaviorData(CreatureEntity creature) {
		this.creature = creature;
		time = creature.getWorld().getTime();
		beginTime = time.getTime();
	}

	public double getElapsedTime() {
		return time.getTime() - beginTime;
	}

	public Entity getClosestPlayer() {
		if (closestPlayer == null) {
			closestPlayer = creature.getWorld().getEntityPool().closest(EntityGroup.PLAYER, creature);
			closestDistanceSquaredToPlayer = creature.distanceSquared(closestPlayer);
		}
		return closestPlayer;
	}

	void beforeStep() {
		closestPlayer = null;
	}

}
