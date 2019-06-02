package com.pixurvival.core.command;

import com.pixurvival.core.World;

/**
 * A CommandExecutor can execute Commands.
 * 
 * @author SharkHendrix
 *
 */
public interface CommandExecutor {

	/**
	 * @return true if this CommandExecutor is an operator and can execute
	 *         operator commands
	 */
	boolean isOperator();

	/**
	 * @return the world concerned by this CommandExecutor
	 */
	World getWorld();
}
