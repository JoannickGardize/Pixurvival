package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.Time;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

public class BehaviorData {

	private Time time;

	@Getter
	@Setter
	private long beginTimeMillis;

	private @Getter CreatureEntity creature;

	private Entity closestPlayer;
	private double closestDistanceSquaredToPlayer;

	@Getter
	@Setter
	private long nextUpdateTimeMillis;

	public BehaviorData(CreatureEntity creature) {
		this.creature = creature;
		time = creature.getWorld().getTime();
		beginTimeMillis = time.getTimeMillis();
	}

	public long getElapsedTimeMillis() {
		return time.getTimeMillis() - beginTimeMillis;
	}

	public Entity getClosestPlayer() {
		findClosestPlayer();
		return closestPlayer;
	}

	public double getClosestDistanceSquaredToPlayer() {
		findClosestPlayer();
		return closestDistanceSquaredToPlayer;
	}

	public void setNextUpdateDelay(long delayMillis) {
		nextUpdateTimeMillis = time.getTimeMillis() + delayMillis;
	}

	void beforeStep() {
		closestPlayer = null;
	}

	private void findClosestPlayer() {
		if (closestPlayer == null) {
			closestPlayer = creature.getWorld().getEntityPool().closest(EntityGroup.PLAYER, creature);
			closestDistanceSquaredToPlayer = creature.distanceSquared(closestPlayer);
		}
	}
}
