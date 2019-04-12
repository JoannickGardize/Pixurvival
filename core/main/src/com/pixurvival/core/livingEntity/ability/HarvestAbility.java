package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.Direction;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.map.HarvestableStructure;

public class HarvestAbility extends WorkAbility {

	private static final long serialVersionUID = 1L;

	@Override
	public AbilityData createAbilityData() {
		return new HarvestAbilityData();
	}

	@Override
	public ActionAnimation getActionAnimation(LivingEntity entity) {
		double angle = entity.angleToward(((HarvestAbilityData) getAbilityData(entity)).getStructure());
		return ActionAnimation.getMoveFromDirection(Direction.closestCardinal(angle));
	}

	@Override
	public void workFinished(LivingEntity entity) {
		World world = entity.getWorld();
		if (world.isServer()) {
			HarvestableStructure structure = ((HarvestAbilityData) getAbilityData(entity)).getStructure();
			if (structure.isHarvested()) {
				return;
			}
			ItemStack[] items = structure.harvest(world.getRandom());
			for (ItemStack itemStack : items) {
				ItemStackEntity itemStackEntity = new ItemStackEntity(itemStack);
				itemStackEntity.getPosition().set(structure.getX(), structure.getY());
				world.getEntityPool().add(itemStackEntity);
				itemStackEntity.spawnRandom();
			}
		}
	}
}
