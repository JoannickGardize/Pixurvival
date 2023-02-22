package com.pixurvival.core.command.impl;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.CollectionUtils;
import com.pixurvival.core.util.Vector2;

import java.util.Collection;
import java.util.stream.Collectors;

public class RespawnCommandProcessor extends CommandProcessor {

    public RespawnCommandProcessor() {
        super(true);
    }

    @Override
    protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
        CommandArgsUtils.checkArgsLength(args, 2, 3);
        Collection<PlayerEntity> players = CommandArgsUtils.playerCollection(executor, args[1]).stream().filter(p -> !p.isAlive()).collect(Collectors.toList());
        Vector2 position = args.length > 2 ? CommandArgsUtils.position(executor, args[2]) : CommandArgsUtils.position(executor);
        players.forEach(p -> p.respawn(position));
        return players.isEmpty() ? "Nobody to respawn." : "Respawned " + CollectionUtils.toString(players);
    }

    @Override
    public Class<?> getAutocompleteArgType(int argIndex) {
        if (argIndex == 1) {
            return PlayerEntity.class;
        } else {
            return null;
        }
    }
}
