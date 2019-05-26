package com.pixurvival.server.console;

import com.esotericsoftware.minlog.Log;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SimpleCommandProcessor implements CommandProcessor {

	private int minArgs;
	private int maxArgs;
	private CommandProcessor action;

	public SimpleCommandProcessor(int numArgs, CommandProcessor action) {
		this(numArgs, numArgs, action);
	}

	@Override
	public void process(String[] args) {
		if (args.length >= minArgs && args.length <= maxArgs) {
			try {
				action.process(args);
			} catch (Exception e) {
				Log.error("Error executing command", e);
			}
		} else {
			Log.warn("Wrong number of args, must be in range [" + minArgs + ", " + maxArgs + "]");
		}
	}

}
