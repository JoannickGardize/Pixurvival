package com.pixurvival.core.command;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class CommandProcessor {

	private boolean requireOperator;

	public void process(CommandExecutor executor, String[] args) throws CommandExecutionException {
		if (!requireOperator || executor.isOperator()) {
			execute(executor, args);
		} else {
			throw new CommandExecutionException("Operator right required");
		}
	}

	protected abstract void execute(CommandExecutor executor, String[] args) throws CommandExecutionException;
}
