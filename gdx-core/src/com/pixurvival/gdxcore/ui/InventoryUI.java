package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;

public class InventoryUI extends Window {

	public InventoryUI() {
		super("Inventory", PixurvivalGame.getSkin());

		Inventory inv = new Inventory(32);
		inv.setSlot(0, new ItemStack(PixurvivalGame.getWorld().getContentPack().getItemsById().get(0), 10));
		inv.setSlot(1, new ItemStack(PixurvivalGame.getWorld().getContentPack().getItemsById().get(1), 1));
		add(new InventoryTable(inv, 8)).expand().fill();
		pack();
	}
}
