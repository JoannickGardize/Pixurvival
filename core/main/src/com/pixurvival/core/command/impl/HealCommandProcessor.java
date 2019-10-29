package com.pixurvival.core.command.impl;

import java.util.Collection;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.CollectionUtils;

public class HealCommandProcessor extends CommandProcessor {

	public HealCommandProcessor() {
		super(true);
	}

	@Override
	protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
		CommandArgsUtils.checkArgsLength(args, 2, 3);
		Collection<PlayerEntity> players = CommandArgsUtils.playerCollection(executor, args[1]);
		float amount = 1000000;
		if (args.length > 2) {
			amount = CommandArgsUtils.getFloat(args[2]);
		}
		for (PlayerEntity player : players) {
			player.takeHeal(amount);
		}
		return "Healed " + CollectionUtils.toString(players) + " for " + amount + " Health points.";
	}

	@Override
	public Class<?> getAutocompleteArgType(int argIndex) {
		if (argIndex == 1) {
			return PlayerEntity.class;
		} else {
			return null;
		}
	}

}
