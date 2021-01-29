package com.pixurvival.core.livingEntity.ability;

import java.io.Serializable;
import java.util.function.Consumer;

import com.pixurvival.core.alteration.Alteration;
import com.pixurvival.core.alteration.StatFormula;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class of all abilities, including creatures and players abilities.
 * 
 * This class is stateless, except the id set by the {@link AbilitySet}
 * containing this ability. This id is used to share the current ability used by
 * an entity between the server and the clients.
 * 
 * @author SharkHendrix
 */
public abstract class Ability implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final byte NONE_ID = -1;

	private @Getter @Setter byte id;

	public AbilityData getAbilityData(LivingEntity entity) {
		return entity.getAbilityData(id);
	}

	/**
	 * Override to create data that this ability needs.
	 * 
	 * @return the object containing the data of this ability, called for each
	 *         {@link LivingEntity} created, during their initializations.
	 */
	public AbilityData createAbilityData() {
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
	 * @param entity
	 *            the entity using this ability
	 * @return The animation to play, or null if no special animation is required.
	 */
	public ActionAnimation getActionAnimation(LivingEntity entity) {
		return null;
	}

	/**
	 * @param entity
	 *            the entity using this ability
	 * @return The item to draw on top of the animation, or null if none to draw.
	 */
	public Item getAnimationItem(LivingEntity entity) {
		return null;
	}

	/**
	 * Called when the entity starts using this ability. If data is needed to start
	 * this ability (e.g. The item to craft), The {@link AbilityData} must be set
	 * correctly before.
	 * 
	 * @param entity
	 * @return true if the ability has started successfully, false otherwise (it
	 *         won't start).
	 */
	public abstract boolean start(LivingEntity entity);

	/**
	 * Called every logic frame by the entity using this ability.
	 * 
	 * @param entity
	 * @return true if the ability has ended, false otherwise.
	 */
	public abstract boolean update(LivingEntity entity);

	public boolean update(LivingEntity entity, AbilityData data) {
		return update(entity);
	}

	/**
	 * Called when the ability is interrupted, or terminated by returning true in
	 * the {@link #update(LivingEntity)} method.
	 * 
	 * @param entity
	 * @return true if the ability has ended successfully, false otherwise (it will
	 *         continue).
	 */
	public abstract boolean stop(LivingEntity entity);

	public void forEachStatFormulas(Consumer<StatFormula> action) {
		// For override
	}

	public void forEachAlteration(Consumer<Alteration> action) {
		// For override
	}

	public Ability copy() {
		throw new UnsupportedOperationException();
	}
}
