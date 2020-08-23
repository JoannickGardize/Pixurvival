package com.pixurvival.core.command.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.livingEntity.PlayerEntity;

public class ListCommandProcessor extends CommandProcessor {

	private Map<String, Function<CommandExecutor, String>> listers = new HashMap<>();

	public ListCommandProcessor() {
		super(false);
		listers.put("creature", identifiedElementLister(Creature.class));
		listers.put("item", identifiedElementLister(Item.class));
		listers.put("player", executor -> Arrays.toString(executor.getWorld().getPlayerEntities().values().stream().map(PlayerEntity::getName).toArray()));
	}

	@Override
	protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
		CommandArgsUtils.checkArgsLength(args, 1, 2);
		if (args.length == 1) {
			return "Use /list with one of the given argument: " + Arrays.toString(listers.keySet().toArray());
		} else {
			return listers.getOrDefault(args[1], e -> "No list of type " + args[1]).apply(executor);
		}
	}

	@Override
	public Class<?> getAutocompleteArgType(int argIndex) {
		return null;
	}

	Function<CommandExecutor, String> identifiedElementLister(Class<? extends IdentifiedElement> type) {
		return executor -> Arrays.toString(executor.getWorld().getContentPack().allNamesOf(type).toArray());
	}
}
