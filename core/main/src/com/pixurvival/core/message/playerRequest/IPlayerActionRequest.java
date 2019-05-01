package com.pixurvival.core.message.playerRequest;

import com.pixurvival.core.livingEntity.PlayerEntity;

/**
 * Main interface for all player actions
 * 
 * @author SharkHendrix
 *
 */
public interface IPlayerActionRequest {

	void apply(PlayerEntity player);

	/**
	 * 
	 * @return True if the client must directly apply locally the action change,
	 *         for visual preview.
	 */
	boolean isClientPreapply();
}
