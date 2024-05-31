package com.pixurvival.core.command.impl;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.CollectionUtils;

import java.util.Collection;

public class DiscoverAllCraftsProcessor extends CommandProcessor {
    public DiscoverAllCraftsProcessor() {
        super(true);
    }

    @Override
    protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
        CommandArgsUtils.checkArgsLength(args, 1, 2);
        Collection<PlayerEntity> targetPlayers = CommandArgsUtils.playerCollectionOrSelf(executor, args, 1);
        targetPlayers.forEach(
                p -> p.getWorld().getContentPack().getItems().forEach(
                        i -> p.getItemCraftDiscovery().discover(i)));
        return "All crafts are now available for " + CollectionUtils.toString(targetPlayers);
    }

    @Override
    public Class<?> getAutocompleteArgType(int argIndex) {
        return null;
    }
}
