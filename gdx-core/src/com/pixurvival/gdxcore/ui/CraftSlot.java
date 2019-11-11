package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;

import lombok.Getter;

public class CraftSlot extends Button {

	private static final Color UNCRAFTABLE_COLOR = new Color(0.8f, 0.3f, 0.3f, 1);

	private ItemStackDrawer itemStackDrawer;
	private @Getter ItemCraft itemCraft;

	public CraftSlot(ItemCraft itemCraft) {
		super(PixurvivalGame.getSkin());
		this.itemCraft = itemCraft;
		itemStackDrawer = new ItemStackDrawer(this, 2);
		itemStackDrawer.setItemStack(new ItemStack(itemCraft.getResult().getItem()));
		addListener(new CraftSlotInputListener(itemCraft));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		setColor(ActionPreconditions.canCraft(PixurvivalGame.getClient().getMyPlayer(), itemCraft) ? Color.WHITE : UNCRAFTABLE_COLOR);
		itemStackDrawer.draw(batch);
	}

}
