package com.pixurvival.core.message;

import java.util.Collection;

import com.pixurvival.core.contentPack.item.ItemCraft;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemCraftAvailable {

	private int[] itemCraftIds;

	public ItemCraftAvailable(Collection<ItemCraft> itemCrafts) {
		itemCraftIds = new int[itemCrafts.size()];
		int i = 0;
		for (ItemCraft itemCraft : itemCrafts) {
			itemCraftIds[i++] = itemCraft.getId();
		}
	}
}
