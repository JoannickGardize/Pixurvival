package com.pixurvival.core.command.impl;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.livingEntity.PlayerEntity;

public class OpCommandProcessor extends CommandProcessor {

    public OpCommandProcessor() {
        super(true);
    }

    @Override
    protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
        CommandArgsUtils.checkArgsLength(args, 2, 2);
        PlayerEntity player = CommandArgsUtils.singlePlayer(executor, args[1]);
        player.setOperator(true);
        return player + " is now operator.";
    }

    @Override
    public Class<?> getAutocompleteArgType(int argIndex) {
        return null;
    }

}
