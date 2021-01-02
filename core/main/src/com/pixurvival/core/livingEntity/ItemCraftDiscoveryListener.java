package com.pixurvival.core.livingEntity;

import java.util.Collection;

import com.pixurvival.core.contentPack.item.ItemCraft;

public interface ItemCraftDiscoveryListener {

	void discovered(Collection<ItemCraft> itemCrafts);
}
