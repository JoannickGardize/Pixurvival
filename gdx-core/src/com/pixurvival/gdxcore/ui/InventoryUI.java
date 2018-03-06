package com.pixurvival.gdxcore.ui;

import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;

public class InventoryUI extends UIWindow {

	public InventoryUI() {
		super("Inventory", PixurvivalGame.getSkin());
		Inventory inv = PixurvivalGame.getClient().getMyInventory();
		add(new InventoryTable(inv, 8)).expand().fill();
		pack();
	}

}
