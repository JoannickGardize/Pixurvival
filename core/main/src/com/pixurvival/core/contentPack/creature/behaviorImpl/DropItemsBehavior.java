package com.pixurvival.core.contentPack.creature.behaviorImpl;

import com.pixurvival.core.contentPack.creature.Behavior;
import com.pixurvival.core.contentPack.creature.BehaviorData;
import com.pixurvival.core.contentPack.elementSet.AllElementSet;
import com.pixurvival.core.contentPack.elementSet.ElementSet;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.validation.annotation.Positive;
import com.pixurvival.core.contentPack.validation.annotation.Valid;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.CreatureEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DropItemsBehavior extends Behavior {

	private static final long serialVersionUID = 1L;

	private BehaviorTarget targetDirection;

	@Valid
	private ElementSet<Item> items = new AllElementSet<>();

	/**
	 * 0 = unlimited
	 */
	@Positive
	private int maxQuantity = 0;

	@Override
	public void begin(CreatureEntity creature) {
		super.begin(creature);
		Inventory inventory = creature.getInventory();
		int sum = 0;
		float angle = getAngle(creature);
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack itemStack = inventory.getSlot(i);
			if (itemStack != null && items.contains(itemStack.getItem())) {
				if (maxQuantity == 0 || sum + itemStack.getQuantity() <= maxQuantity) {
					spawnItemStack(creature, angle, itemStack);
					sum += itemStack.getQuantity();
					inventory.setSlot(i, null);
				} else if (sum < maxQuantity) {
					int dropQuantity = maxQuantity - sum;
					spawnItemStack(creature, angle, itemStack.copy(dropQuantity));
					inventory.setSlot(i, itemStack.sub(dropQuantity));
					break;
				}
			}
		}
		creature.setForward(false);
	}

	@Override
	protected void step(CreatureEntity creature) {
		creature.getBehaviorData().setTaskFinished(true);
		creature.getBehaviorData().setNextUpdateDelayMillis(BehaviorData.DEFAULT_STANDBY_DELAY);
	}

	private void spawnItemStack(CreatureEntity creature, float angle, ItemStack itemStack) {
		ItemStackEntity entity = new ItemStackEntity(itemStack);
		entity.getPosition().set(creature.getPosition());
		creature.getWorld().getEntityPool().addNew(entity);
		entity.spawn(angle);
	}

	private float getAngle(CreatureEntity creature) {
		Entity target = targetDirection.getEntityGetter().apply(creature);
		return target == null ? creature.getWorld().getRandom().nextAngle() : creature.angleToward(target);
	}
}
