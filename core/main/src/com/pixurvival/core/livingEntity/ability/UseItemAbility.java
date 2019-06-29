package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;


public class UseItemAbility extends WorkAbility {

	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean start(LivingEntity entity) {
		super.start(entity);
		ItemStack itemStack  = ((UseItemAbilityData) getAbilityData(entity)).getItemStack();
		if (itemStack == null) {
			return false;
		}
		return true;
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
			ItemStack itemStack = data.getItemStack();
			short slotIndex = data.getSlotIndex();
			if(PlayerInventory.HELD_ITEM_STACK_INDEX == slotIndex) {
				if(itemStack.getQuantity() == 1) {
					((PlayerEntity) entity).getInventory().setHeldItemStack(null);
				} else {
					((PlayerEntity) entity).getInventory().setHeldItemStack(itemStack.sub(1));
				}
			} else {
				Inventory inventory = ((InventoryHolder) entity).getInventory();
				inventory.remove(itemStack);
			}
		}
	}

}
