package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.input.InputAction;

import lombok.Getter;

public class InventoryUI extends UIWindow {

	private @Getter InventoryTable inventoryTable;

	public InventoryUI() {
		super("inventory");
		Inventory inv = PixurvivalGame.getClient().getMyInventory();
		inventoryTable = new InventoryTable(inv, 8) {
			@Override
			public Actor newSlot(Inventory inventory, int index) {
				InventorySlot slot = new InventorySlot(inventory, index);
				if (index < 10) {
					InputAction action = InputAction.valueOf("INVENTORY" + (index + 1));
					slot.setShortcutDrawer(new ShortcutDrawer(slot, action, ShortcutDrawer.BOTTOM));
				}
				return slot;
			}
		};
		add(inventoryTable).expand().fill();
	}

}
