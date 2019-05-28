package com.pixurvival.core.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.item.Item;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;

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

	public static PlayerEntity getPlayer(CommandExecutor executor, String arg) throws CommandExecutionException {
		for (Entity e : executor.getWorld().getEntityPool().get(EntityGroup.PLAYER)) {
			PlayerEntity player = (PlayerEntity) e;
			if (player.getName().equals(arg)) {
				return player;
			}
		}
		throw new CommandExecutionException("No player found with name " + arg);
	}

	public static ItemStack getItemStack(CommandExecutor executor, String arg) throws CommandExecutionException {
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

	public static void checkArgsLength(String[] args, int min, int max) throws CommandExecutionException {
		if (args.length < min || args.length > max) {
			throw new CommandExecutionException("Wrong number of args : " + args.length + ", must be in range [" + min + ", " + "]");
		}
	}
}
