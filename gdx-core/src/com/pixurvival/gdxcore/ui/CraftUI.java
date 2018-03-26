package com.pixurvival.gdxcore.ui;

import java.util.List;

import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;

public class CraftUI extends UIWindow {

	public CraftUI() {
		super("Crafting", PixurvivalGame.getSkin());
		List<ItemCraft> itemCrafts = PixurvivalGame.getWorld().getContentPack().getItemCraftsById();
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
