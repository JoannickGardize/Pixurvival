package com.pixurvival.core.alteration;

import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DropItemsAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	@Valid
	private ElementSet<Item> items = new AllElementSet<>();

	/**
	 * 0 = unlimited
	 */
	@Positive
	private int maxQuantity = 0;

	@Override
	public void uniqueApply(TeamMember source, TeamMember entity) {
		if (!(entity instanceof LivingEntity)) {
			return;
		}
		LivingEntity livingEntity = (LivingEntity) entity;
		livingEntity.prepareTargetedAlteration();
		Inventory inventory = livingEntity.getInventory();
		int sum = 0;
		float angle = livingEntity.getPosition().angleToward(livingEntity.getTargetPosition());
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack itemStack = inventory.getSlot(i);
			if (itemStack != null && items.contains(itemStack.getItem())) {
				if (maxQuantity == 0 || sum + itemStack.getQuantity() <= maxQuantity) {
					spawnItemStack(livingEntity, angle, itemStack);
					sum += itemStack.getQuantity();
					inventory.setSlot(i, null);
				} else if (sum < maxQuantity) {
					int dropQuantity = maxQuantity - sum;
					spawnItemStack(livingEntity, angle, itemStack.copy(dropQuantity));
					inventory.setSlot(i, itemStack.sub(dropQuantity));
					break;
				}
			}
		}
	}

	private void spawnItemStack(Entity entity, float angle, ItemStack itemStack) {
		ItemStackEntity itemEntity = new ItemStackEntity(itemStack);
		itemEntity.getPosition().set(entity.getPosition());
		entity.getWorld().getEntityPool().addNew(itemEntity);
		itemEntity.spawn(angle);
	}
}
