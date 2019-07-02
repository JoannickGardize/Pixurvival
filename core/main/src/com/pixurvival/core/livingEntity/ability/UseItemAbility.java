package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.item.EdibleItem;
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
		EdibleItem edibleItem = ((UseItemAbilityData) getAbilityData(entity)).getEdibleItem();
		if (edibleItem == null) {
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
		// TODO
		if (entity.getWorld().isServer()) {
			UseItemAbilityData data = (UseItemAbilityData) getAbilityData(entity);
			EdibleItem edibleItem = data.getEdibleItem();
			short slotIndex = data.getSlotIndex();
			if (PlayerInventory.HELD_ITEM_STACK_INDEX == slotIndex) {
				PlayerInventory inventory = ((PlayerEntity) entity).getInventory();
				ItemStack heldItemStack = inventory.getHeldItemStack();
				if (heldItemStack.getQuantity() == 1) {
					inventory.setHeldItemStack(null);
				} else {
					inventory.setHeldItemStack(heldItemStack.sub(1));
				}
			} else {
				Inventory inventory = ((InventoryHolder) entity).getInventory();
				inventory.remove(edibleItem, 1);
			}
		}
	}

}
