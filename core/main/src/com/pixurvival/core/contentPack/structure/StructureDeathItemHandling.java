package com.pixurvival.core.contentPack.structure;

import java.util.function.Consumer;

import com.pixurvival.core.item.InventoryHolder;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

// TODO merge this with PlayerDeathItemHandling
@AllArgsConstructor
public enum StructureDeathItemHandling {
	DROP(h -> {
		for (int i = 0; i < h.getInventory().size(); i++) {
			dropItemOnDeath(h, h.getInventory().getSlot(i));
		}
		removeAll(h);
	}),
	REMOVE(StructureDeathItemHandling::removeAll);

	private @Getter Consumer<InventoryHolder> handler;

	private static void dropItemOnDeath(InventoryHolder holder, ItemStack itemStack) {
		if (itemStack != null) {
			ItemStackEntity itemStackEntity = new ItemStackEntity(itemStack);
			holder.getWorld().getEntityPool().addNew(itemStackEntity);
			itemStackEntity.getPosition().set(holder.getPosition());
			itemStackEntity.spawnRandom();
		}
	}

	private static void removeAll(InventoryHolder holder) {
		holder.getInventory().removeAll();
	}
}