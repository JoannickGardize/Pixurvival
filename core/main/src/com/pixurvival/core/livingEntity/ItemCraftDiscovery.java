package com.pixurvival.core.livingEntity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.util.VarLenNumberIO;

/**
 * Represents the actual items and crafts discovered and so available by a
 * player.
 * 
 * @author SharkHendrix
 *
 */
public class ItemCraftDiscovery implements InventoryListener {

	private Collection<ItemCraftDiscoveryListener> listeners;
	private Set<Item> discoveredItems;
	private Set<ItemCraft> discoveredItemCrafts;
	private Collection<ItemCraft> missingItemCrafts;

	public ItemCraftDiscovery(Inventory inventory, Collection<ItemCraft> allItemCrafts) {
		listeners = new ArrayList<>();
		discoveredItems = new HashSet<>();
		discoveredItemCrafts = new HashSet<>();
		missingItemCrafts = new ArrayList<>(allItemCrafts);
		inventory.addListener(this);
		discover(null);
		discoveredItems.remove(null);

	}

	public int[] getDiscovereditemCraftIds() {
		return discoveredItemCrafts.stream().mapToInt(ItemCraft::getId).toArray();
	}

	public void addListener(ItemCraftDiscoveryListener listener) {
		listeners.add(listener);
	}

	public boolean isDiscovered(ItemCraft craft) {
		return discoveredItemCrafts.contains(craft);
	}

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		if (newItemStack != null) {
			discover(newItemStack.getItem());
		}
	}

	public void discover(Item item) {
		if (discoveredItems.add(item)) {
			List<ItemCraft> newItemCrafts = new ArrayList<>();
			Iterator<ItemCraft> iterator = missingItemCrafts.iterator();
			while (iterator.hasNext()) {
				ItemCraft craft = iterator.next();
				if (craft.discover(discoveredItems)) {
					iterator.remove();
					discoveredItemCrafts.add(craft);
					newItemCrafts.add(craft);
				}
			}
			if (!newItemCrafts.isEmpty()) {
				listeners.forEach(l -> l.discovered(newItemCrafts));
			}
		}
	}

	public void write(ByteBuffer buffer) {
		VarLenNumberIO.writePositiveVarInt(buffer, discoveredItems.size());
		for (Item item : discoveredItems) {
			VarLenNumberIO.writePositiveVarInt(buffer, item.getId());
		}
	}

	public void apply(ByteBuffer buffer, List<Item> allItems) {
		int length = VarLenNumberIO.readPositiveVarInt(buffer);
		for (int i = 0; i < length; i++) {
			discover(allItems.get(VarLenNumberIO.readPositiveVarInt(buffer)));
		}
	}
}
