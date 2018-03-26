package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;

public class CraftSlot extends Button implements InventoryListener {

	private static final Color UNCRAFTABLE_COLOR = new Color(0.8f, 0.3f, 0.3f, 1);

	private ItemStackDrawer itemStackDrawer;
	private ItemCraft itemCraft;

	public CraftSlot(ItemCraft itemCraft) {
		super(PixurvivalGame.getSkin());
		this.itemCraft = itemCraft;
		itemStackDrawer = new ItemStackDrawer(this, 2);
		System.out.println(itemCraft.getResult().getItem());
		itemStackDrawer.setItemStack(new ItemStack(itemCraft.getResult().getItem()));
		PixurvivalGame.getClient().getMyInventory().addListener(this);
		slotChanged(PixurvivalGame.getClient().getMyInventory(), 0);
		addListener(new CraftSlotInputListener(itemCraft));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		itemStackDrawer.draw(batch);

	}

	@Override
	public void slotChanged(Inventory inventory, int slotIndex) {
		if (inventory.fastContainsAll(itemCraft.getRecipes())) {
			setColor(Color.WHITE);
		} else {
			setColor(UNCRAFTABLE_COLOR);
		}
	}
}
