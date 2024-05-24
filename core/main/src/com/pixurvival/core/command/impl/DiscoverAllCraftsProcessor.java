package com.pixurvival.core.command.impl;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;

public class DiscoverAllCraftsProcessor extends CommandProcessor {
    public DiscoverAllCraftsProcessor() {
        super(true);
    }

    @Override
    protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
        CommandArgsUtils.checkArgsLength(args, 2, 3);
        executor.getWorld().getPlayerEntities().values().forEach(p ->
                p.getWorld().getContentPack().getItems().forEach(
                        i -> p.getItemCraftDiscovery().discover(i)));
        return "All crafts are now available for all players.";
    }

    @Override
    public Class<?> getAutocompleteArgType(int argIndex) {
        return null;
    }
}
