package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityGroup;
import com.pixurvival.core.Time;
import com.pixurvival.core.World;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

public class BehaviorData {

	public static final long MAX_UPDATE_DELAY_RELATIVE_TO_SPEED = 1000;
	public static final long CHANGE_CONDITION_CHECK_DELAY = 300;

	public static final long DEFAULT_STANDBY_DELAY = 500;

	private Time time;

	@Getter
	@Setter
	private long beginTimeMillis;

	private long previousChangeConditionCheck;

	private @Getter CreatureEntity creature;

	private Entity closestPlayer;
	private double closestDistanceSquaredToPlayer;

	/**
	 * Temps de la prochaine mise à jour du {@link Behavior}. Si cette valeur n'est
	 * pas modifié, ou est inférieur ou temps actuel du {@link World}, il en
	 * résultera une mise à jour du behavior à chaque frame.
	 */
	@Getter
	@Setter
	private long nextUpdateTimeMillis;

	public BehaviorData(CreatureEntity creature) {
		this.creature = creature;
		time = creature.getWorld().getTime();
		beginTimeMillis = time.getTimeMillis();
		previousChangeConditionCheck = time.getTimeMillis();
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

	public void setNextUpdateDelayMillis(long delayMillis) {
		nextUpdateTimeMillis = time.getTimeMillis() + delayMillis;
	}

	/**
	 * Fixe le temps de la prochaine mise à jour du {@link Behavior} de manière à ce
	 * qu'elle corresponde au temps qu'il faut pour que la créature parcourt la
	 * distance passé en paramètre, relatif donc à la vitesse de la créature.
	 * 
	 * @param targetDistance
	 *            La distance à parcourir utilisé pour calculer le temps de la
	 *            prochaine mise à jour.
	 */
	public void setNextUpdateDelayRelativeToSpeed(double targetDistance) {
		double speed = creature.getSpeed();
		long delayMillis = Math.min(Time.secToMillis(speed > 0 ? targetDistance / speed : 0), MAX_UPDATE_DELAY_RELATIVE_TO_SPEED);
		setNextUpdateDelayMillis(delayMillis);
	}

	public boolean mustCheckChangeCondition() {
		if (time.getTimeMillis() - previousChangeConditionCheck <= CHANGE_CONDITION_CHECK_DELAY) {
			previousChangeConditionCheck = time.getTimeMillis();
			return true;
		} else {
			return false;
		}
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
