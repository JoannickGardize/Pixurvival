package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.Direction;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.MapStructure;

public class DeconstructAbility extends WorkAbility {
	private static final long serialVersionUID = 1L;

	@Override
	public AbilityData createAbilityData() {
		return new DeconstructAbilityData();
	}

	@Override
	public ActionAnimation getActionAnimation(LivingEntity entity) {
		MapStructure structure = ((DeconstructAbilityData) getAbilityData(entity)).getStructure();
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
			MapStructure structure = ((DeconstructAbilityData) getAbilityData(entity)).getStructure();
			MapStructure actual = world.getMap().tileAt(structure.getTileX(), structure.getTileY()).getStructure();
			if (structure == actual) {
				structure.getChunk().removeStructure(structure.getTileX(), structure.getTileY());
				structure.onDeath();
				Item item = structure.getDefinition().getDeconstructionItem();
				if (item != null) {
					ItemStackEntity.spawn(world, new ItemStack[] { new ItemStack(item) }, structure.getPosition());
				}
			}
		}
	}
}
