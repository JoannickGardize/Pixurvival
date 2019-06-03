package com.pixurvival.gdxcore.ui;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.gdxcore.PixurvivalGame;

public class CraftUI extends UIWindow {

	public CraftUI() {
		super("crafting");
		List<ItemCraft> itemCrafts = PixurvivalGame.getWorld().getContentPack().getItemCrafts();
		Inventory inventory = new Inventory(itemCrafts.size());
		add(new InventoryTable(inventory, 8) {
			@Override
			public Actor newSlot(Inventory inventory, int index) {
				return new CraftSlot(itemCrafts.get(index));
			}
		}).expand().fill();

		pack();
	}

}
