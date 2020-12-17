package com.pixurvival.core.livingEntity.alteration;

import com.pixurvival.core.contentPack.WeightedValueProducer;
import com.pixurvival.core.contentPack.validation.annotation.Length;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.team.TeamMember;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddItemAlteration extends UniqueAlteration {

	private static final long serialVersionUID = 1L;

	@Valid
	private StatFormula repeat = new StatFormula();
	@Valid
	@Length(min = 1)
	private WeightedValueProducer<ItemStack> itemStacks = new WeightedValueProducer<>();
	private boolean dropRemainder;

	@Override
	public void uniqueApply(TeamMember source, TeamMember entity) {
		if (entity instanceof InventoryHolder) {
			float repeatValue = repeat.getValue(source);
			for (int i = 0; i < repeatValue; i++) {
				ItemStack itemStack = itemStacks.next(source.getWorld().getRandom());
				ItemStack remainder = ((InventoryHolder) entity).getInventory().add(itemStack);
				if (dropRemainder && remainder != null) {
					ItemStackEntity itemStackEntity = new ItemStackEntity(remainder);
					itemStackEntity.getPosition().set(source.getPosition());
					entity.getWorld().getEntityPool().addNew(itemStackEntity);
				}
			}
		}
	}

}
