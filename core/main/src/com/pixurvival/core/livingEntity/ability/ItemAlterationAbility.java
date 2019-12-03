package com.pixurvival.core.livingEntity.ability;

import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.LivingEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemAlterationAbility extends AlterationAbility {

	private static final long serialVersionUID = 1L;

	private ItemStack ammunition = new ItemStack();

	@Override
	public boolean canFire(LivingEntity entity) {
		return ammunition.getItem() == null || removeAmmunition(entity);
	}

	private boolean removeAmmunition(LivingEntity entity) {
		if (entity instanceof InventoryHolder) {
			Inventory inventory = ((InventoryHolder) entity).getInventory();
			return inventory != null && inventory.remove(ammunition);
		}
		return true;
	}
}
