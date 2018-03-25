package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.Direction;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.map.HarvestableStructure;

import lombok.Getter;

public class HarvestingActivity extends WorkActivity {

	private @Getter HarvestableStructure structure;
	private ActionAnimation animation;

	public HarvestingActivity(PlayerEntity entity, HarvestableStructure structure) {
		super(entity, structure.getDefinition().getHarvestingTime());
		this.structure = structure;
		animation = ActionAnimation.getMoveFromDirection(Direction.closestCardinal(entity.angleTo(structure)));
	}

	@Override
	public ActionAnimation getActionAnimation() {
		return animation;
	}

	@Override
	public byte getId() {
		return Activity.HARVESTING_ID;
	}

	@Override
	public void onFinished() {
		World world = getEntity().getWorld();
		if (world.isServer() && !structure.isHarvested()) {
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
