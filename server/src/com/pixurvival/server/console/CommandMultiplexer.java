package com.pixurvival.server.console;

import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.command.CommandArgsUtils;

public class CommandMultiplexer implements CommandProcessor {

	private Map<String, CommandProcessor> processors = new HashMap<>();

	public void addProcessor(String name, CommandProcessor processor) {
		processors.put(name, processor);
	}

	@Override
	public boolean process(String[] args) throws Exception {
		if (args.length == 0) {
			Log.warn("Cannot treat empty command");
			return true;
		}
		CommandProcessor processor = processors.get(args[0]);
		if (processor == null) {
			// Log.warn("Unknown command : " + args[0]);
			return false;
		} else {
			processor.process(CommandArgsUtils.subArgs(args));
			return true;
		}
	}
}
