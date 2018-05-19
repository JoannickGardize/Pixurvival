package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;

public class InventoryUI extends UIWindow {

	public InventoryUI() {
		super("inventory");
		Inventory inv = PixurvivalGame.getClient().getMyInventory();
		add(new ScrollPane(new InventoryTable(inv, 8), PixurvivalGame.getSkin())).expand().fill();
		pack();
	}

}
