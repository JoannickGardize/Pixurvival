package com.pixurvival.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;

import lombok.experimental.UtilityClass;

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

	public static Entity singlePlayer(CommandExecutor executor, String arg) throws CommandExecutionException {
		Collection<PlayerEntity> collection = playerCollection(executor, arg);
		if (collection.size() == 1) {
			return collection.iterator().next();
		} else {
			throw new CommandExecutionException("Expected one player but multiple target match with " + arg);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
				return Collections.unmodifiableCollection((Collection) executor.getWorld().getEntityPool().get(EntityGroup.PLAYER));
			default:
				throw new CommandExecutionException("Unknown special reference : " + arg);
			}
		}
		for (Entity e : executor.getWorld().getEntityPool().get(EntityGroup.PLAYER)) {
			PlayerEntity player = (PlayerEntity) e;
			if (player.getName().equals(arg)) {
				return Collections.singleton(player);
			}
		}
		throw new CommandExecutionException("No player found with name " + arg);
	}

	/**
	 * Position in format x;y OR position of player reference OR special
	 * reference to cursor
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
			}
		}
		if (arg.matches("\\d+(\\.\\d+)?;\\d+(\\.\\d+)?")) {
			String[] split = arg.split(";");
			return new Vector2(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
		} else {
			return singlePlayer(executor, arg).getPosition();
		}
	}

	public static ItemStack itemStack(CommandExecutor executor, String arg) throws CommandExecutionException {
		String[] split = arg.split(":");
		Item item = executor.getWorld().getContentPack().get(Item.class, split[0]);
		if (item == null) {
			throw new CommandExecutionException("No item with name : " + split[0]);
		}
		if (split.length == 1) {
			return new ItemStack(item);
		} else {
			if (!split[1].matches("\\d+")) {
				throw new CommandExecutionException("Item quantity must be an integer");
			}
			return new ItemStack(item, Integer.parseInt(split[1]));
		}
	}

	public static ItemStack[] itemStacks(CommandExecutor executor, String arg) throws CommandExecutionException {
		String[] split = arg.split("\\,");
		ItemStack[] result = new ItemStack[split.length];
		for (int i = 0; i < split.length; i++) {
			result[i] = itemStack(executor, split[i]);
		}
		return result;
	}

	public static void checkArgsLength(String[] args, int min, int max) throws CommandExecutionException {
		if (args.length < min || args.length > max) {
			throw new CommandExecutionException("Wrong number of args : " + args.length + ", must be in range [" + min + ", " + max + "]");
		}
	}
}
