package com.pixurvival.gdxcore.ui;

import java.util.List;

import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.gdxcore.PixurvivalGame;

public class CraftUI extends UIWindow {

	public CraftUI() {
		super("crafting");
		List<ItemCraft> itemCrafts = PixurvivalGame.getWorld().getContentPack().getItemCrafts();
		Inventory inventory = new Inventory(itemCrafts.size());
		add(new InventoryTable(inventory, 8) {
			@Override
			public void addSlot(Inventory inventory, int index) {
				add(new CraftSlot(itemCrafts.get(index)));
			}
		}).expand().fill();

		pack();
	}

}