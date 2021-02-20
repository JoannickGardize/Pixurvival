package com.pixurvival.core.contentPack.structure;

import java.util.function.Consumer;

import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.item.MultiInventoryHolder;

import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO merge this with PlayerDeathItemHandling
@AllArgsConstructor
public enum StructureDeathItemHandling {
	DROP(h -> {
		h.forEachInventory(inv -> inv.foreachItemStacks(i -> dropItemOnDeath(h, i)));
		removeAll(h);
	}),
	REMOVE(StructureDeathItemHandling::removeAll);

	private @Getter Consumer<MultiInventoryHolder> handler;

	private static void dropItemOnDeath(MultiInventoryHolder holder, ItemStack itemStack) {
		ItemStackEntity itemStackEntity = new ItemStackEntity(itemStack);
		holder.getWorld().getEntityPool().addNew(itemStackEntity);
		itemStackEntity.getPosition().set(holder.getPosition());
		itemStackEntity.spawnRandom();
	}

	private static void removeAll(MultiInventoryHolder holder) {
		holder.forEachInventory(Inventory::removeAll);
	}
}