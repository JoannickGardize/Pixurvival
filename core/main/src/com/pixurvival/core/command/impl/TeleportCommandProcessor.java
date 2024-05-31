package com.pixurvival.core.command.impl;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.CollectionUtils;
import com.pixurvival.core.util.Vector2;

import java.util.Collection;

public class TeleportCommandProcessor extends CommandProcessor {

    public TeleportCommandProcessor() {
        super(true);
    }

    @Override
    protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
        CommandArgsUtils.checkArgsLength(args, 3, 3);
        Collection<PlayerEntity> players = CommandArgsUtils.playerCollection(executor, args[1]);
        Vector2 destination = CommandArgsUtils.position(executor, args[2]);
        for (PlayerEntity player : players) {
            player.teleport(destination);
        }
        return "Teleported " + CollectionUtils.toString(players);
    }

    @Override
    public Class<?> getAutocompleteArgType(int argIndex) {
        switch (argIndex) {
            case 1:
                return PlayerEntity.class;
            default:
                return null;
        }
    }
}
