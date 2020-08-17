package com.pixurvival.core.command.impl;

import com.pixurvival.core.command.CommandArgsUtils;
import com.pixurvival.core.command.CommandExecutionException;
import com.pixurvival.core.command.CommandExecutor;
import com.pixurvival.core.command.CommandProcessor;
import com.pixurvival.core.contentPack.creature.Creature;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.util.Vector2;

public class SpawnCommandProcessor extends CommandProcessor {

	public SpawnCommandProcessor() {
		super(true);
	}

	@Override
	protected String execute(CommandExecutor executor, String[] args) throws CommandExecutionException {
		CommandArgsUtils.checkArgsLength(args, 2, 4);
		Creature creature = CommandArgsUtils.contentPackElement(executor, Creature.class, args[1]);
		Vector2 position = args.length > 2 ? CommandArgsUtils.position(executor, args[2]) : CommandArgsUtils.position(executor);
		boolean owned = args.length > 3 && "owned".equals(args[3]);
		CreatureEntity creatureEntity = new CreatureEntity(creature);
		creatureEntity.getPosition().set(position);
		if (owned && executor instanceof PlayerEntity) {
			creatureEntity.setMaster((PlayerEntity) executor);
		}
		executor.getWorld().getEntityPool().create(creatureEntity);
		return "Spawned " + creature.getName();
	}

	@Override
	public Class<?> getAutocompleteArgType(int argIndex) {
		if (argIndex == 1) {
			return Creature.class;
		} else {
			return null;
		}
	}

}
