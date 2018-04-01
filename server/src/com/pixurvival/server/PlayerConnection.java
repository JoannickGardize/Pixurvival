package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerConnection extends Connection implements InventoryListener {

	private boolean logged = false;
	private PlayerEntity playerEntity = null;
	private boolean gameReady = false;
	private boolean inventoryChanged = true;

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		inventoryChanged = true;
	}
}
