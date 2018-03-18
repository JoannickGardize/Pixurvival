package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.World;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.map.HarvestableStructure;

public class HarvestingActivity extends Activity {

	private AliveEntity entity;
	private HarvestableStructure structure;
	private double progress;

	public HarvestingActivity(AliveEntity entity, HarvestableStructure structure) {
		this.entity = entity;
		this.structure = structure;
		progress = 0;
	}

	@Override
	public boolean canMove() {
		return false;
	}

	@Override
	public void update() {
		World world = entity.getWorld();
		progress += world.getTime().getDeltaTime();
		if (progress >= structure.getDefinition().getHarvestingTime()) {
			structure.setHarvested(true);
			ItemStack[] items = structure.getDefinition().getItemReward().produce(world.getRandom());
		}

	}

}
