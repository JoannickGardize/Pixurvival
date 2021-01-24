package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.Scene2dUtils;

import lombok.Getter;
import lombok.Setter;

public class CraftSlot extends Button {

	private static final Color UNCRAFTABLE_COLOR = new Color(0.8f, 0.3f, 0.3f, 1);

	private ItemStackDrawer itemStackDrawer;
	private @Getter ItemCraft itemCraft;
	private @Setter @Getter boolean newlyDiscovered = true;
	private boolean canCraft = false;

	public CraftSlot(ItemCraft itemCraft, boolean newlyDiscovered) {
		super(PixurvivalGame.getSkin());
		this.itemCraft = itemCraft;
		this.newlyDiscovered = newlyDiscovered;
		itemStackDrawer = new ItemStackDrawer(this, 2);
		itemStackDrawer.setItemStack(new ItemStack(itemCraft.getResult().getItem()));
		addListener(new CraftSlotInputListener(this));
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		// TODO do not compute this each frame
		boolean canCraftNow = ActionPreconditions.canCraft(PixurvivalGame.getClient().getMyPlayer(), itemCraft);
		setColor(canCraftNow ? Color.WHITE : UNCRAFTABLE_COLOR);
		if (!canCraft && canCraftNow) {
			addAction(Scene2dUtils.yellowLightning());
		}
		canCraft = canCraftNow;
		itemStackDrawer.draw(batch);
		if (newlyDiscovered) {
			PixurvivalGame.getOverlayFont().draw(batch, "New!", getX() + 5, getY() + getHeight() - 5);
		}
	}

	@Override
	public float getPrefHeight() {
		return getPrefSize();
	}

	@Override
	public float getPrefWidth() {
		return getPrefSize();
	}

	@Override
	public float getMinHeight() {
		return 20;
	}

	@Override
	public float getMinWidth() {
		return 20;
	}

	private float getPrefSize() {
		return MathUtils.clamp((Gdx.graphics.getWidth() / 1920f) * 60f, 20f, 60f);
	}
}
