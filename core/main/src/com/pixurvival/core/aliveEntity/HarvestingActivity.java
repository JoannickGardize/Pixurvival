package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.Direction;
import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.map.HarvestableStructure;

import lombok.Getter;
import lombok.Setter;

public class HarvestingActivity extends Activity {

	private AliveEntity entity;
	private @Getter HarvestableStructure structure;
	private @Getter @Setter double progressTime;
	private ActionAnimation animation;

	public HarvestingActivity(AliveEntity entity, HarvestableStructure structure) {
		this.entity = entity;
		this.structure = structure;
		progressTime = 0;
		animation = ActionAnimation.getMoveFromDirection(Direction.closestCardinal(entity.angleTo(structure)));
	}

	public double getProgress() {
		return progressTime / structure.getDefinition().getHarvestingTime();
	}

	@Override
	public boolean canMove() {
		return false;
	}

	@Override
	public ActionAnimation getActionAnimation() {
		return animation;
	}

	@Override
	public void update() {
		World world = entity.getWorld();
		progressTime += world.getTime().getDeltaTime();
		if (progressTime >= structure.getDefinition().getHarvestingTime()) {
			entity.setActivity(Activity.NONE);
			if (world.isServer() && !structure.isHarvested()) {
				ItemStack[] items = structure.harvest(world.getRandom());
				for (ItemStack itemStack : items) {
					ItemStackEntity itemStackEntity = new ItemStackEntity(itemStack);
					itemStackEntity.getPosition().set(structure.getX(), structure.getY());
					entity.getWorld().getEntityPool().add(itemStackEntity);
					itemStackEntity.spawnRandom();
				}
			}
		}
	}

	@Override
	public byte getId() {
		return Activity.HARVESTING_ID;
	}

}
