package com.pixurvival.core.command;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {

	private Map<String, CommandProcessor> commands = new HashMap<>();

	public CommandManager() {
		commands.put("give", new GiveItemCommandProcessor());
	}

	public String process(CommandExecutor executor, String[] args) {
		CommandProcessor processor = commands.get(args[0]);
		if (processor == null) {
			return "Unknown command " + args[0];
		}
		try {
			processor.process(executor, args);
			return null;
		} catch (CommandExecutionException e) {
			return e.getMessage();
		}
	}
}
