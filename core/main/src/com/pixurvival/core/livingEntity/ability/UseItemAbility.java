package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.contentPack.item.EdibleItem;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.PlayerInventory;
import com.pixurvival.core.livingEntity.alteration.Alteration;

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
		return ActionAnimation.WORK_DOWN;
	}

	@Override
	public Item getAnimationItem(LivingEntity entity) {
		UseItemAbilityData data = (UseItemAbilityData) getAbilityData(entity);
		return data.getEdibleItem();
	}

	@Override
	public void workFinished(LivingEntity entity) {
		if (entity.getWorld().isServer()) {
			UseItemAbilityData data = (UseItemAbilityData) getAbilityData(entity);
			EdibleItem edibleItem = data.getEdibleItem();
			short slotIndex = data.getSlotIndex();
			if (removeItem(entity, edibleItem, slotIndex)) {
				for (Alteration alteration : edibleItem.getAlterations()) {
					alteration.apply(entity, entity);
				}
			}
		}
	}

	private boolean removeItem(LivingEntity entity, EdibleItem edibleItem, short slotIndex) {
		PlayerInventory inventory = ((PlayerEntity) entity).getInventory();
		if (slotIndex == PlayerInventory.HELD_ITEM_STACK_INDEX) {
			ItemStack heldItemStack = inventory.getHeldItemStack();
			if (heldItemStack != null && heldItemStack.getItem() == edibleItem) {
				inventory.setHeldItemStack(heldItemStack.sub(1));
				return true;
			}
		} else {
			ItemStack itemStack = inventory.getSlot(slotIndex);
			if (itemStack != null && itemStack.getItem() == edibleItem) {
				inventory.setSlot(slotIndex, itemStack.sub(1));
				return true;
			}
		}
		// If the item is not on the target slot, try to take it anywhere
		return inventory.remove(edibleItem, 1);
	}

}
