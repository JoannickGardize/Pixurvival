package com.pixurvival.core.contentPack.creature;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.effect.TargetType;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntitySearchResult;
import com.pixurvival.core.entity.EntitySearchUtils;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.time.Time;

import lombok.Getter;
import lombok.Setter;

public class BehaviorData {

	// TODO avoid this ?
	public static final float TARGET_SEARCH_RADIUS = 64;

	public static final long MAX_UPDATE_DELAY_RELATIVE_TO_SPEED = 1000;

	public static final long DEFAULT_STANDBY_DELAY = 500;

	private Time time;

	@Getter
	@Setter
	private long beginTimeMillis;

	private @Getter CreatureEntity creature;

	private boolean closestEnnemyComputed = false;
	private Entity closestEnnemy;

	@Getter
	@Setter
	private boolean tookDamage = false;

	@Getter
	@Setter
	private boolean taskFinished = false;

	@Getter
	@Setter
	private boolean nothingToDo = false;

	/**
	 * Temps de la prochaine mise à jour du {@link Behavior}. Si cette valeur n'est
	 * pas modifié, ou est inférieur ou temps actuel du {@link World}, il en
	 * résultera une mise à jour du behavior à chaque frame.
	 */
	@Getter
	@Setter
	private long nextUpdateTimeMillis;

	@Getter
	@Setter
	private Object customData = null;

	public BehaviorData(CreatureEntity creature) {
		this.creature = creature;
		time = creature.getWorld().getTime();
		reset();
	}

	public void reset() {
		beginTimeMillis = time.getTimeMillis();
		closestEnnemyComputed = false;
		closestEnnemy = null;
		tookDamage = false;
		taskFinished = false;
		nothingToDo = false;
		customData = null;
	}

	public long getElapsedTimeMillis() {
		return time.getTimeMillis() - beginTimeMillis;
	}

	public Entity getClosestEnnemy() {
		findClosestEnnemy();
		return closestEnnemy;
	}

	public void setNextUpdateDelayMillis(long delayMillis) {
		nextUpdateTimeMillis = time.getTimeMillis() + delayMillis;
	}

	public void forceUpdate() {
		nextUpdateTimeMillis = creature.getWorld().getTime().getTimeMillis();
	}

	/**
	 * Fixe le temps de la prochaine mise à jour du {@link Behavior} de manière à ce
	 * qu'elle corresponde au temps qu'il faut pour que la créature parcourt la
	 * distance passé en paramètre, relatif donc à la vitesse de la créature. la
	 * distance considéré ne sera jamais supérieur à
	 * {@link CreatureEntity#DEFAULT_OBSTACLE_VISION_DISTANCE}.
	 * 
	 * @param targetDistance
	 *            La distance à parcourir utilisé pour calculer le temps de la
	 *            prochaine mise à jour.
	 */
	public void setNextUpdateDelayRelativeToSpeed(float targetDistance) {
		internalSetNextUpdateDelayRelativeToSpeed(Math.min(CreatureEntity.DEFAULT_OBSTACLE_VISION_DISTANCE, targetDistance));
	}

	public void setNextUpdateDelayRelativeToSpeed() {
		internalSetNextUpdateDelayRelativeToSpeed(CreatureEntity.DEFAULT_OBSTACLE_VISION_DISTANCE);
	}

	private void internalSetNextUpdateDelayRelativeToSpeed(float targetDistance) {
		float speed = creature.getSpeed();
		setNextUpdateDelayMillis(Math.min(DEFAULT_STANDBY_DELAY, speed > 0 ? Time.secToMillis(targetDistance / speed) : DEFAULT_STANDBY_DELAY));

	}

	public void updateForNextChangeConditionCheck() {
		tookDamage = false;
		taskFinished = false;
		nothingToDo = false;
	}

	void beforeStep() {
		closestEnnemy = null;
		closestEnnemyComputed = false;
	}

	private void findClosestEnnemy() {
		if (!closestEnnemyComputed) {
			EntitySearchResult result = EntitySearchUtils.findClosest(creature, TargetType.ALL_ENEMIES, TARGET_SEARCH_RADIUS);
			closestEnnemy = result.getEntity();
			closestEnnemyComputed = true;
		}
	}

}
