package com.pixurvival.core.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.pixurvival.core.command.impl.GiveItemCommandProcessor;
import com.pixurvival.core.command.impl.SpawnCommandProcessor;
import com.pixurvival.core.command.impl.TeleportCommandProcessor;
import com.pixurvival.core.contentPack.IdentifiedElement;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.PlayerEntity;

public class CommandManager {

	private Map<String, CommandProcessor> commands = new HashMap<>();

	public CommandManager() {
		commands.put("give", new GiveItemCommandProcessor());
		commands.put("teleport", new TeleportCommandProcessor());
		commands.put("tp", new TeleportCommandProcessor());
		commands.put("spawn", new SpawnCommandProcessor());
	}

	public String process(CommandExecutor executor, String[] args) {
		CommandProcessor processor = commands.get(args[0]);
		if (processor == null) {
			return "Unknown command " + args[0];
		}
		try {
			return processor.process(executor, args);
		} catch (CommandExecutionException e) {
			return e.getMessage();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String autocomplete(CommandExecutor executor, String input) {
		String[] args = CommandArgsUtils.splitArgs(input);
		if (args.length < 2 || args[0].length() < 2 || args[0].charAt(0) != '/') {
			return input;
		}
		CommandProcessor processor = commands.get(args[0].substring(1));
		if (processor == null) {
			return input;
		}
		Class<?> argType = processor.getAutocompleteArgType(args.length - 1);
		if (argType == null) {
			return null;
		}
		Iterator<String> stringIterator;
		if (IdentifiedElement.class.isAssignableFrom(argType)) {
			stringIterator = executor.getWorld().getContentPack().allNamesOf((Class<? extends IdentifiedElement>) argType).iterator();
		} else if (argType == PlayerEntity.class) {
			stringIterator = playerNameIterator((Collection) executor.getWorld().getEntityPool().get(EntityGroup.PLAYER));
		} else {
			return input;
		}
		String argToAutocomplete = args[args.length - 1];
		String autocompletedArg = findAutoComplete(stringIterator, argToAutocomplete);
		if (autocompletedArg == null) {
			return input;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length - 1; i++) {
			sb.append(addQuotes(args[i]));
			sb.append(" ");
		}
		sb.append(addQuotes(autocompletedArg));
		return sb.toString();

	}

	private String findAutoComplete(Iterator<String> stringSet, String argToAutocomplete) {
		String autocompletedArg = null;
		int smallestSize = Integer.MAX_VALUE;
		while (stringSet.hasNext()) {
			String string = stringSet.next();
			if (string.startsWith(argToAutocomplete) && string.length() < smallestSize) {
				autocompletedArg = string;
				smallestSize = string.length();
			}
		}
		return autocompletedArg;
	}

	private String addQuotes(String arg) {
		if (arg.contains(" ")) {
			return "\"" + arg + "\"";
		} else {
			return arg;
		}
	}

	private Iterator<String> playerNameIterator(Collection<PlayerEntity> players) {
		return new Iterator<String>() {

			Iterator<PlayerEntity> playerIterator = players.iterator();

			@Override
			public boolean hasNext() {
				return playerIterator.hasNext();
			}

			@Override
			public String next() {
				return playerIterator.next().getName();
			}
		};
	}

}
