package com.pixurvival.core.command;

import com.pixurvival.core.contentPack.NamedIdentifiedElement;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;
import lombok.experimental.UtilityClass;

import java.util.*;

@UtilityClass
public class CommandArgsUtils {

    public static String[] splitArgs(String input) {
        List<String> result = new ArrayList<>();
        boolean escapingSpaces = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == ' ' && !escapingSpaces) {
                if (sb.length() > 0) {
                    result.add(sb.toString());
                    sb.setLength(0);
                }
            } else if (c == '\"') {
                escapingSpaces = !escapingSpaces;
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            result.add(sb.toString());
            sb.setLength(0);
        }
        return result.toArray(new String[result.size()]);
    }

    public static String[] subArgs(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Cannot create subargs of empty array");
        } else if (args.length == 1) {
            return new String[0];
        } else {
            return Arrays.copyOfRange(args, 1, args.length);
        }
    }

    public static PlayerEntity asPlayer(CommandExecutor executor, String arg) throws CommandExecutionException {
        if (executor instanceof Entity) {
            return (PlayerEntity) executor;
        } else {
            throw new CommandExecutionException("Invalid call to " + arg + " : you are not a player");
        }
    }

    public static Collection<PlayerEntity> playerCollectionOrSelf(CommandExecutor executor, String[] args, int argNumber) throws CommandExecutionException {
        if (args.length < argNumber + 1) {
            if (!(executor instanceof PlayerEntity)) {
                throw new CommandExecutionException("Error: you are not a player.");
            }
            return Collections.singleton((PlayerEntity) executor);
        } else {
            return CommandArgsUtils.playerCollection(executor, args[argNumber]);
        }
    }

    public static PlayerEntity singlePlayer(CommandExecutor executor, String arg) throws CommandExecutionException {
        Collection<PlayerEntity> collection = playerCollection(executor, arg);
        if (collection.size() == 1) {
            return collection.iterator().next();
        } else {
            throw new CommandExecutionException("Expected one player but multiple target match with " + arg);
        }
    }

    public static Collection<PlayerEntity> playerCollection(CommandExecutor executor, String arg) throws CommandExecutionException {
        if (arg.startsWith("@")) {
            switch (arg) {
                case "@me":
                    return Collections.singleton(asPlayer(executor, "@me"));
                case "@closest":
                    PlayerEntity player = (PlayerEntity) asPlayer(executor, "@closest").findClosest(EntityGroup.PLAYER);
                    if (player == null) {
                        throw new CommandExecutionException("@closest : no other players");
                    }
                    return Collections.singleton(player);
                case "@everyone":
                case "@all":
                    return Collections.unmodifiableCollection(executor.getWorld().getPlayerEntities().values());
                default:
                    throw new CommandExecutionException("Unknown special reference : " + arg);
            }
        }
        for (PlayerEntity player : executor.getWorld().getPlayerEntities().values()) {
            if (player.getName().equals(arg)) {
                return Collections.singleton(player);
            }
        }
        throw new CommandExecutionException("No player found with name " + arg);
    }

    /**
     * Position in format x;y OR position of player reference OR special reference
     * to cursor
     *
     * @param executor
     * @param arg
     * @return
     * @throws CommandExecutionException
     */
    public static Vector2 position(CommandExecutor executor, String arg) throws CommandExecutionException {
        if (arg.startsWith("@")) {
            switch (arg) {
                case "@cursor":
                case "@target":
                    PlayerEntity player = asPlayer(executor, "@cursor");
                    return player.getTargetPosition();
                case "@me":
                    return asPlayer(executor, "@me").getPosition();
                case "@closest":
                    Entity e = asPlayer(executor, "@closest").findClosest(EntityGroup.PLAYER);
                    if (e == null) {
                        throw new CommandExecutionException("@closest : no other players");
                    }
                    return e.getPosition();
            }
        }
        if (arg.matches("\\-?\\d+(\\.\\d+)?;\\-?\\d+(\\.\\d+)?")) {
            String[] split = arg.split(";");
            return new Vector2(Float.parseFloat(split[0]), Float.parseFloat(split[1]));
        } else {
            return singlePlayer(executor, arg).getPosition();
        }
    }

    public static Vector2 position(CommandExecutor executor) throws CommandExecutionException {
        PlayerEntity player = asPlayer(executor, "@cursor");
        return player.getTargetPosition();
    }

    public static ItemStack itemStack(CommandExecutor executor, String arg) throws CommandExecutionException {
        String[] split = arg.split(":");
        Item item = contentPackElement(executor, Item.class, split[0]);
        if (split.length == 1) {
            return new ItemStack(item);
        } else {
            if (!split[1].matches("\\d+")) {
                throw new CommandExecutionException("Item quantity must be an integer");
            }
            return new ItemStack(item, Integer.parseInt(split[1]));
        }
    }

    public static <T extends NamedIdentifiedElement> T contentPackElement(CommandExecutor executor, Class<T> type, String arg) throws CommandExecutionException {
        T element = executor.getWorld().getContentPack().get(type, arg);
        if (element == null) {
            throw new CommandExecutionException("No " + type.getSimpleName() + " with name " + arg);
        }
        return element;
    }

    public static ItemStack[] itemStacks(CommandExecutor executor, String arg) throws CommandExecutionException {
        String[] split = arg.split("\\,");
        ItemStack[] result = new ItemStack[split.length];
        for (int i = 0; i < split.length; i++) {
            result[i] = itemStack(executor, split[i].trim());
        }
        return result;
    }

    public static float getFloat(String arg) throws CommandExecutionException {
        if (!arg.matches("\\d+(\\.d+)?")) {
            throw new CommandExecutionException(arg + " is not a number.");
        }
        return Float.parseFloat(arg);
    }

    public static void checkArgsLength(String[] args, int min, int max) throws CommandExecutionException {
        if (args.length < min || args.length > max) {
            throw new CommandExecutionException("Wrong number of args : " + args.length + ", must be in range [" + min + ", " + max + "]");
        }
    }
}
