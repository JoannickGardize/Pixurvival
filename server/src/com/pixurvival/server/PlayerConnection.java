package com.pixurvival.server;

import com.esotericsoftware.kryonet.Connection;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.livingEntity.Equipment;
import com.pixurvival.core.livingEntity.EquipmentListener;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.livingEntity.stats.StatListener;
import com.pixurvival.core.livingEntity.stats.StatValue;

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
		System.out.println("equip");
		playerDataChanged = true;
	}

	@Override
	public void changed(StatValue statValue) {
	}

	@Override
	public void baseChanged(StatValue statValue) {
		System.out.println("base");
		playerDataChanged = true;
	}

}
