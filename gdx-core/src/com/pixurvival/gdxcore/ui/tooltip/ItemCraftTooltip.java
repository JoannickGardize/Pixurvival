package com.pixurvival.gdxcore.ui.tooltip;

import java.util.Locale;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.pixurvival.core.contentPack.ContentPack;
import com.pixurvival.core.contentPack.TranslationKey;
import com.pixurvival.core.contentPack.item.ItemCraft;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.DrawUtils;

import lombok.Getter;

public class ItemCraftTooltip extends Table implements InventoryListener {

	private static @Getter ItemCraftTooltip instance = new ItemCraftTooltip();

	public static final float ITEM_WIDTH = 20;

	private ItemCraft itemCraft;

	private ItemCraftTooltip() {
		setTouchable(Touchable.disabled);
		setVisible(false);
		setBackground(PixurvivalGame.getSkin().get("panel", Drawable.class));
		defaults().fill().pad(2);
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible) {
			itemCraft = null;
		}
		super.setVisible(visible);
	}

	public void setItemCraft(ItemCraft itemCraft) {
		if (this.itemCraft == itemCraft) {
			return;
		}
		this.itemCraft = itemCraft;
		build();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		DrawUtils.setTooltipPosition(this);
		super.draw(batch, parentAlpha);
	}

	private void build() {
		if (itemCraft == null) {
			return;
		}
		Inventory inv = PixurvivalGame.getClient().getMyInventory();
		clearChildren();
		Locale locale = PixurvivalGame.getClient().getCurrentLocale();
		ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();
		StringBuilder quantitySB = new StringBuilder();
		for (ItemStack itemStack : itemCraft.getRecipes()) {
			quantitySB.setLength(0);
			int myTotal = inv.totalOf(itemStack.getItem());
			quantitySB.append(contentPack.getTranslation(locale, itemStack.getItem(), TranslationKey.ITEM_NAME)).append(" ");
			quantitySB.append(myTotal).append(" / ").append(itemStack.getQuantity());
			Texture itemTexture = PixurvivalGame.getContentPackTextures().getItem(itemStack.getItem().getId()).getTexture();
			add(new Image(itemTexture)).size(ITEM_WIDTH, ITEM_WIDTH);
			String style = myTotal >= itemStack.getQuantity() ? "white" : "red";
			Label lineLabel = new Label(quantitySB.toString(), PixurvivalGame.getSkin(), style);
			lineLabel.setAlignment(Align.right);
			add(lineLabel);
			add().expand();
			row();
		}
		ItemTooltip itemTooltip = new ItemTooltip(false);
		itemTooltip.setItem(itemCraft.getResult().getItem());
		add(itemTooltip).expand().colspan(3);
		pack();
		invalidate();
	}

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		if (isVisible()) {
			build();
		}
	}
}
