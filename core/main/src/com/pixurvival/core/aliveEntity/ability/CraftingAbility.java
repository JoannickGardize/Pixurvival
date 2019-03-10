package com.pixurvival.core.aliveEntity.ability;

import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;

public class CraftingAbility extends WorkAbility {

	@Override
	public void finished(PlayerEntity entity) {
		if (entity.getWorld().isServer()) {
			Inventory inventory = entity.getInventory();
			CraftingAbilityData data = (CraftingAbilityData) getAbilityData(entity);
			if (inventory.remove(data.getItemCraft().getRecipes())) {
				ItemStack craftedItem = data.getItemCraft().getResult();
				ItemStack rest = inventory.add(craftedItem);
				if (rest != null) {
					ItemStackEntity itemStackEntity = new ItemStackEntity(rest);
					entity.getWorld().getEntityPool().add(itemStackEntity);
					itemStackEntity.getPosition().set(entity.getPosition());
					itemStackEntity.spawnRandom();
				}
			}
		}
	}

}
