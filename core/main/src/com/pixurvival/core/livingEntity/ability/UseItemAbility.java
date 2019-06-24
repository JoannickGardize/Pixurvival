package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.LivingEntity;


public class UseItemAbility extends WorkAbility {

	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean start(LivingEntity entity) {
		//TODO
		super.start(entity);
		ItemStack itemStack  = ((UseItemAbilityData) getAbilityData(entity)).getItemStack();
		if (itemStack == null) {
			return false;
		}
		Inventory inventory = ((InventoryHolder) entity).getInventory();
		return inventory.contains(itemStack);
	}
	
	@Override
	public AbilityData createAbilityData() {
		return new UseItemAbilityData();
	}
	
	@Override
	public ActionAnimation getActionAnimation(LivingEntity entity) {
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public void workFinished(LivingEntity entity) {
		//TODO
		if (entity.getWorld().isServer()) {
			UseItemAbilityData data = (UseItemAbilityData) getAbilityData(entity);
			Inventory inventory = ((InventoryHolder) entity).getInventory();
			inventory.remove(data.getItemStack());
		}
	}

}
