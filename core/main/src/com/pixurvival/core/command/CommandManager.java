package com.pixurvival.core.command;

import java.util.HashMap;
import java.util.Map;

import com.pixurvival.core.command.impl.GiveItemCommandProcessor;
import com.pixurvival.core.command.impl.TeleportCommandProcessor;

public class CommandManager {

	private Map<String, CommandProcessor> commands = new HashMap<>();

	public CommandManager() {
		commands.put("give", new GiveItemCommandProcessor());
		commands.put("teleport", new TeleportCommandProcessor());
		commands.put("tp", new TeleportCommandProcessor());
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
}