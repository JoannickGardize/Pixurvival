package com.pixurvival.core.command.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.CollectionUtils;

public class GiveItemCommandProcessor extends CommandProcessor {

	public GiveItemCommandProcessor() {
		super(true);
	}

	@Override
	protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
		CommandArgsUtils.checkArgsLength(args, 2, 3);
		ItemStack[] itemStacks = CommandArgsUtils.itemStacks(executor, args[1]);
		Collection<PlayerEntity> targets;
		if (args.length < 3) {
			if (!(executor instanceof PlayerEntity)) {
				throw new CommandExecutionException("No player target specified");
			}
			targets = Collections.singleton((PlayerEntity) executor);
		} else {
			targets = CommandArgsUtils.playerCollection(executor, args[2]);
		}
		for (ItemStack itemStack : itemStacks) {
			for (PlayerEntity player : targets) {
				player.getInventory().add(itemStack.copy());
			}
		}
		return "Given " + Arrays.toString(itemStacks) + " to " + CollectionUtils.toString(targets);
	}

	@Override
	public Class<?> getAutocompleteArgType(int argIndex) {
		switch (argIndex) {
		case 1:
			return Item.class;
		case 2:
			return PlayerEntity.class;
		default:
			return null;
		}
	}

}
