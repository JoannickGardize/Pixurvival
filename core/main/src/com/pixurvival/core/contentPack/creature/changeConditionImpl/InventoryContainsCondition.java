package com.pixurvival.core.contentPack.creature.changeConditionImpl;

import com.pixurvival.core.contentPack.creature.ChangeCondition;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.util.FloatComparison;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryContainsCondition extends ChangeCondition {

	private static final long serialVersionUID = 1L;

	@Valid
	private ElementSet<Item> items = new AllElementSet<>();
	private FloatComparison operator = FloatComparison.GREATER_THAN;
	@Positive
	private int value;

	@Override
	public boolean test(CreatureEntity creature) {
		Inventory inventory = creature.getInventory();
		int sum = 0;
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack itemStack = inventory.getSlot(i);
			if (itemStack != null && items.contains(itemStack.getItem())) {
				sum += itemStack.getQuantity();
			}
		}
		return operator.test(sum, value);
	}

}
