package com.pixurvival.core.livingEntity;

import com.pixurvival.core.contentPack.item.ItemCraft;

import java.util.Collection;

public interface ItemCraftDiscoveryListener {

    void discovered(Collection<ItemCraft> itemCrafts);
}
