package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;

public class CraftingActivity extends WorkActivity {

	private ItemCraft itemCraft;

	public CraftingActivity(PlayerEntity entity, ItemCraft craft) {
		super(entity, craft.getDuration());
	}

	@Override
	public void onFinished() {
		Inventory inventory = getEntity().getInventory();
		if(inventory.contains(itemCraft.getRecipes())) {
			inventory.
		}
	}
}
