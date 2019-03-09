package com.pixurvival.core.aliveEntity.ability;

import java.nio.ByteBuffer;

import com.pixurvival.core.aliveEntity.AliveEntity;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public abstract class Ability<T extends AliveEntity<T>> {

	private @Getter @Setter(AccessLevel.PACKAGE) int id;

	public Object getAbilityData(AliveEntity<?> entity) {
		return entity.getAbilityData(id);
	}

	/**
	 * Override to create initialize an object data that this ability needs.
	 * 
	 * @return the object containing the data of this ability, called for each
	 *         {@link AliveEntity} created, during their initializations.
	 */
	public Object createAbilityData() {
		return null;
	}

	/**
	 * Indicates if the entity can move or not when using this ability.
	 * 
	 * @return true if the entity can move when using this ability, false otherwise.
	 */
	public boolean canMove() {
		return true;
	}

	/**
	 * @return The animation to play, or null if no special animation is required.
	 */
	public ActionAnimation getActionAnimation() {
		return null;
	}

	/**
	 * Called when the entity starts using this ability.
	 * 
	 * @param entity
	 */
	public abstract void start(T entity);

	/**
	 * Called every logic frame by the entity using this ability.
	 * 
	 * @param entity
	 * @return true if the ability has ended, false otherwise.
	 */
	public abstract boolean update(T entity);

	/**
	 * Called when the ability is interrupted, or terminated by returning true in
	 * the {@link #update(AliveEntity)} method.
	 * 
	 * @param entity
	 */
	public abstract void stop(T entity);

	public abstract void writeUpdate(ByteBuffer buffer);

	public abstract void applyUpdate(ByteBuffer buffer);

}
