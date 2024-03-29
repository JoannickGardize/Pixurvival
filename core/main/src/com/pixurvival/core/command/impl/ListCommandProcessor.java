package com.pixurvival.core.command.impl;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.contentPack.item.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ListCommandProcessor extends CommandProcessor {

    private Map<String, Function<CommandExecutor, String>> listers = new HashMap<>();

    public ListCommandProcessor() {
        super(false);
        listers.put("creature", identifiedElementLister(Creature.class));
        listers.put("item", identifiedElementLister(Item.class));
        listers.put("player", executor -> Arrays.toString(executor.getWorld().getPlayerEntities().values().stream().map(p -> p.getName() + (p.isAlive() ? "" : " (dead)")).toArray()));
    }

    @Override
    protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
        CommandArgsUtils.checkArgsLength(args, 1, 2);
        if (args.length == 1) {
            return "Use /list with one of the given argument: " + Arrays.toString(listers.keySet().toArray());
        } else {
            return listers.getOrDefault(args[1], e -> "No list of type " + args[1]).apply(executor);
        }
    }

    @Override
    public Class<?> getAutocompleteArgType(int argIndex) {
        return null;
    }

    Function<CommandExecutor, String> identifiedElementLister(Class<? extends NamedIdentifiedElement> type) {
        return executor -> Arrays.toString(executor.getWorld().getContentPack().allNamesOf(type).toArray());
    }
}
