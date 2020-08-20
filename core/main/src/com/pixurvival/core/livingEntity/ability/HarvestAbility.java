package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.Direction;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;

public class HarvestAbility extends WorkAbility {

	private static final long serialVersionUID = 1L;

	@Override
	public AbilityData createAbilityData() {
		return new HarvestAbilityData();
	}

	@Override
	public ActionAnimation getActionAnimation(LivingEntity entity) {
		MapStructure structure = ((HarvestAbilityData) getAbilityData(entity)).getStructure();
		if (structure == null) {
			return null;
		} else {
			float angle = entity.angleToward(structure);
			return ActionAnimation.getWorkFromDirection(Direction.closestCardinalDirection(angle));
		}
	}

	@Override
	public void workFinished(LivingEntity entity) {
		World world = entity.getWorld();
		if (world.isServer()) {
			HarvestableMapStructure structure = ((HarvestAbilityData) getAbilityData(entity)).getStructure();
			if (structure == null) {
				// TODO remove this when ability is restored correctly after repository update
				return;
			}
			MapStructure actual = world.getMap().tileAt(structure.getTileX(), structure.getTileY()).getStructure();
			if (structure == actual && ActionPreconditions.canInteract(entity, structure) && !structure.isHarvested()) {
				ItemStack[] items = structure.harvest(world.getRandom());
				ItemStackEntity.spawn(world, items, structure.getPosition());
			}
		}
	}
}
