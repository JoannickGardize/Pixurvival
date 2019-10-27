package com.pixurvival.core.command;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class CommandProcessor {

	private boolean requireOperator;

	public String process(CommandExecutor executor, String[] args) throws CommandExecutionException {
		if (!requireOperator || executor.isOperator()) {
			return execute(executor, args);
		} else {
			throw new CommandExecutionException("Operator right required");
		}
	}

	protected abstract String execute(CommandExecutor executor, String[] args) throws CommandExecutionException;

	public abstract Class<?> getAutocompleteArgType(int argIndex);
}
