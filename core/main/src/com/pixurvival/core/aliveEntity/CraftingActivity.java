package com.pixurvival.core.aliveEntity;

import com.pixurvival.core.Direction;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;

import lombok.Getter;

public class CraftingActivity extends WorkActivity {

	private @Getter ItemCraft itemCraft;
	private @Getter ActionAnimation actionAnimation;

	public CraftingActivity(PlayerEntity entity, ItemCraft craft) {
		super(entity, craft.getDuration());
		this.itemCraft = craft;
		actionAnimation = ActionAnimation.getMoveFromDirection(Direction.closestCardinal(entity.getMovingAngle()));
	}

	@Override
	public byte getId() {
		return Activity.CRAFTING_ACTIVITY_ID;
	}

	@Override
	public void onFinished() {
		if (getEntity().getWorld().isServer()) {
			Inventory inventory = getEntity().getInventory();
			if (inventory.remove(itemCraft.getRecipes())) {
				ItemStack craftedItem = itemCraft.getResult();
				ItemStack rest = inventory.add(craftedItem);
				if (rest != null) {
					ItemStackEntity entity = new ItemStackEntity(rest);
					getEntity().getWorld().getEntityPool().add(entity);
					entity.getPosition().set(getEntity().getPosition());
					entity.spawnRandom();
				}
			}

		}
	}
}
