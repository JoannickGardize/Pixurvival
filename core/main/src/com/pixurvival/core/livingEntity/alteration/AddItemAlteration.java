package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddItemAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	private ItemStack itemStack = new ItemStack();
	private boolean dropRemainder;

	@Override
	public void uniqueApply(TeamMember source, LivingEntity entity) {
		if (entity instanceof InventoryHolder) {
			ItemStack remainder = ((InventoryHolder) entity).getInventory().add(itemStack);
			if (dropRemainder && remainder != null) {
				ItemStackEntity itemStackEntity = new ItemStackEntity(remainder);
				itemStackEntity.getPosition().set(source.getPosition());
				entity.getWorld().getEntityPool().add(itemStackEntity);
			}
		}
	}

}
