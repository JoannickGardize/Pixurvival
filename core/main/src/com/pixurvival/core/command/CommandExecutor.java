package com.pixurvival.core.command;

import com.pixurvival.core.World;

public interface CommandExecutor {

	boolean isOperator();

	World getWorld();
}
