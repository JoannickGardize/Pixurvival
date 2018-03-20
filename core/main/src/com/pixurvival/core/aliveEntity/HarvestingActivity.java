package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.World;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.message.Direction;

public class HarvestingActivity extends Activity {

	private AliveEntity entity;
	private HarvestableStructure structure;
	private double progress;
	private ActionAnimation animation;

	public HarvestingActivity(AliveEntity entity, HarvestableStructure structure) {
		this.entity = entity;
		this.structure = structure;
		progress = 0;
		animation = ActionAnimation.getMoveFromDirection(Direction.closestCardinal(entity.angleTo(structure)));
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
		progress += world.getTime().getDeltaTime();
		if (progress >= structure.getDefinition().getHarvestingTime()) {
			entity.setActivity(Activity.NONE);
			if (world.isServer()) {
				ItemStack[] items = structure.harvest(world.getRandom());
				for (ItemStack itemStack : items) {

				}
			}
		}

	}

}
