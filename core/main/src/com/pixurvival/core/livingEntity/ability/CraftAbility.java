package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.LivingEntity;

public class CraftAbility extends WorkAbility {

	@Override
	public boolean start(LivingEntity entity) {
		super.start(entity);
		Inventory inventory = ((InventoryHolder) entity).getInventory();
		ItemCraft itemCraft = ((CraftAbilityData) getAbilityData(entity)).getItemCraft();
		return inventory.contains(itemCraft.getRecipes());
	}

	@Override
	public AbilityData createAbilityData() {
		return new CraftAbilityData();
	}

	@Override
	public void workFinished(LivingEntity entity) {
		if (entity.getWorld().isServer()) {
			Inventory inventory = ((InventoryHolder) entity).getInventory();
			CraftAbilityData data = (CraftAbilityData) getAbilityData(entity);
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
