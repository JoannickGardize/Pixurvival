package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;

public class InventoryUI extends Window {

	private boolean initialized = false;

	public InventoryUI() {
		super("Inventory", PixurvivalGame.getSkin());
	}

	@Override
	public void act(float delta) {
		if (!initialized) {
			Inventory inv = PixurvivalGame.getClient().getMyInventory();
			if (inv != null) {
				add(new InventoryTable(inv, 8)).expand().fill();
				pack();
				initialized = true;
			}
		}

		super.act(delta);
	}
}
