package com.pixurvival.core.livingEntity.ability;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.LivingEntity;

public class CraftAbility extends WorkAbility {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean start(LivingEntity entity) {
		if (!entity.getWorld().isServer()) {
			return true;
		}
		super.start(entity);
		ItemCraft itemCraft = ((CraftAbilityData) getAbilityData(entity)).getItemCraft();
		if (itemCraft == null) {
			return false;
		}
		Inventory inventory = ((InventoryHolder) entity).getInventory();
		if (itemCraft.getRecipes() == null) {
			Log.warn("itemCraft invalide : " + itemCraft);
			return false;
		}
		return inventory.contains(itemCraft.getRecipes());
	}

	@Override
	public AbilityData createAbilityData() {
		return new CraftAbilityData();
	}

	@Override
	public ActionAnimation getActionAnimation(LivingEntity entity) {
		return ActionAnimation.WORK_DOWN;
	}

	@Override
	public Item getAnimationItem(LivingEntity entity) {
		CraftAbilityData data = (CraftAbilityData) getAbilityData(entity);
		return data.getItemCraft().getResult().getItem();
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
