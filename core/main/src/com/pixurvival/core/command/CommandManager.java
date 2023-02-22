package com.pixurvival.core.command;

import com.pixurvival.core.command.impl.*;
import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.livingEntity.PlayerEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommandManager {

    private Map<String, CommandProcessor> commands = new HashMap<>();

    public CommandManager() {
        commands.put("give", new GiveItemCommandProcessor());
        commands.put("teleport", new TeleportCommandProcessor());
        commands.put("tp", new TeleportCommandProcessor());
        commands.put("spawn", new SpawnCommandProcessor());
        commands.put("heal", new HealCommandProcessor());
        commands.put("op", new OpCommandProcessor());
        commands.put("operator", new OpCommandProcessor());
        ListCommandProcessor listCommandProcessor = new ListCommandProcessor();
        commands.put("list", listCommandProcessor);
        commands.put("ls", listCommandProcessor);
        commands.put("respawn", new RespawnCommandProcessor());
        commands.put("revive", new RespawnCommandProcessor());
    }

    public String process(CommandExecutor executor, String[] args) {
        CommandProcessor processor = commands.get(args[0]);
        if (processor == null) {
            return "Unknown command " + args[0];
        }
        try {
            return processor.process(executor, args);
        } catch (CommandExecutionException e) {
            return e.getMessage();
        }
    }

    @SuppressWarnings({"unchecked"})
    public String autocomplete(CommandExecutor executor, String input) {
        String[] args = CommandArgsUtils.splitArgs(input);
        if (args.length < 2 || args[0].length() < 2 || args[0].charAt(0) != '/') {
            return input;
        }
        CommandProcessor processor = commands.get(args[0].substring(1));
        if (processor == null) {
            return input;
        }
        Class<?> argType = processor.getAutocompleteArgType(args.length - 1);
        if (argType == null) {
            return input;
        }
        Iterator<String> stringIterator;
        if (NamedIdentifiedElement.class.isAssignableFrom(argType)) {
            stringIterator = executor.getWorld().getContentPack().allNamesOf((Class<? extends NamedIdentifiedElement>) argType).iterator();
        } else if (argType == PlayerEntity.class) {
            stringIterator = playerNameIterator(executor.getWorld().getPlayerEntities().values());
        } else {
            return input;
        }
        String previousElements = args[args.length - 1];
        int lastElementIndex = previousElements.lastIndexOf(',');
        String lastElement;
        if (lastElementIndex != -1 && lastElementIndex < previousElements.length() - 1) {
            lastElement = previousElements.substring(lastElementIndex + 1);
            previousElements = previousElements.substring(0, lastElementIndex + 1);
        } else {
            lastElement = previousElements;
            previousElements = "";
        }
        String autocompletedArg = findAutoComplete(stringIterator, lastElement);
        if (autocompletedArg == null) {
            return input;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length - 1; i++) {
            sb.append(addQuotes(args[i]));
            sb.append(" ");
        }
        sb.append(addQuotes(previousElements + autocompletedArg));
        return sb.toString();

    }

    private String findAutoComplete(Iterator<String> stringSet, String argToAutocomplete) {
        String autocompletedArg = null;
        int smallestSize = Integer.MAX_VALUE;
        while (stringSet.hasNext()) {
            String string = stringSet.next();
            if (string.startsWith(argToAutocomplete) && string.length() < smallestSize) {
                autocompletedArg = string;
                smallestSize = string.length();
            }
        }
        return autocompletedArg;
    }

    private String addQuotes(String arg) {
        if (arg.contains(" ")) {
            return "\"" + arg + "\"";
        } else {
            return arg;
        }
    }

    private Iterator<String> playerNameIterator(Collection<PlayerEntity> players) {
        return new Iterator<String>() {

            Iterator<PlayerEntity> playerIterator = players.iterator();

            @Override
            public boolean hasNext() {
                return playerIterator.hasNext();
            }

            @Override
            public String next() {
                return playerIterator.next().getName();
            }
        };
    }

}
