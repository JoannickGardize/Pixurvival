package com.pixurvival.core.command;

import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.PlayerEntity;

public class GiveItemCommandProcessor extends CommandProcessor {

	public GiveItemCommandProcessor() {
		super(true);
	}

	@Override
	protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
		CommandArgsUtils.checkArgsLength(args, 2, 3);
		ItemStack itemStack = CommandArgsUtils.getItemStack(executor, args[1]);
		PlayerEntity target;
		if (args.length < 3) {
			if (!(executor instanceof PlayerEntity)) {
				throw new CommandExecutionException("No player target specified");
			}
			target = (PlayerEntity) executor;
		} else {
			target = CommandArgsUtils.getPlayer(executor, args[2]);
		}
		target.getInventory().add(itemStack);
		return new StringBuilder("Given ").append(itemStack.toMessageString()).append(" to ").append(target.getName()).toString();
	}

}
