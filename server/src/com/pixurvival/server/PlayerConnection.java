package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.pixurvival.core.aliveEntity.Equipment;
import com.pixurvival.core.aliveEntity.EquipmentListener;
import com.pixurvival.core.aliveEntity.PlayerEntity;
import com.pixurvival.core.aliveEntity.StatListener;
import com.pixurvival.core.aliveEntity.StatValue;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerConnection extends Connection implements InventoryListener, EquipmentListener, StatListener {

	private boolean logged = false;
	private PlayerEntity playerEntity = null;
	private boolean gameReady = false;
	private boolean worldReady = false;
	private boolean inventoryChanged = true;
	private boolean playerDataChanged = true;

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		inventoryChanged = true;
	}

	@Override
	public void equipmentChanged(Equipment equipment, int equipmentIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		playerDataChanged = true;
	}

	@Override
	public void changed(StatValue statValue) {
	}

	@Override
	public void baseChanged(StatValue statValue) {
		playerDataChanged = true;
	}

}
