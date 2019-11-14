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
import com.pixurvival.core.contentPack.sprite.ActionAnimation;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.DrawUtils;

import lombok.Getter;

public class ItemCraftTooltip extends Table implements InventoryListener {

	private static @Getter ItemCraftTooltip instance = new ItemCraftTooltip();

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
		// Duration and recipes title
		add(RepresenterUtils.labelledValue("hud.itemCraft.duration", RepresenterUtils.time(itemCraft.getDuration()))).expand().colspan(3);
		row();
		ContentPack contentPack = PixurvivalGame.getWorld().getContentPack();
		Locale locale = PixurvivalGame.getClient().getCurrentLocale();
		if (itemCraft.getRequiredStructure() != null) {
			Structure structure = itemCraft.getRequiredStructure();
			add(new Label(PixurvivalGame.getString("hud.itemCraft.require"), PixurvivalGame.getSkin(), "white")).expand().colspan(3);
			row();
			if (structure.getSpriteSheet() != null) {
				Texture texture = PixurvivalGame.getContentPackTextures().getAnimationSet(structure.getSpriteSheet()).get(ActionAnimation.DEFAULT).getTexture(0);
				add(new Image(texture)).size(RepresenterUtils.ITEM_WIDTH, RepresenterUtils.ITEM_WIDTH);
			} else {
				add();
			}
			add(new Label(contentPack.getTranslation(locale, structure, TranslationKey.NAME), PixurvivalGame.getSkin(), "white"));
			row();

		}
		add(new Label(PixurvivalGame.getString("hud.itemCraft.recipes"), PixurvivalGame.getSkin(), "white")).expand().colspan(3);
		// Add Recipes
		StringBuilder quantitySB = new StringBuilder();
		for (ItemStack itemStack : itemCraft.getRecipes()) {
			row();
			quantitySB.setLength(0);
			int myTotal = inv.totalOf(itemStack.getItem());
			quantitySB.append(myTotal).append(" / ").append(itemStack.getQuantity());
			Texture itemTexture = PixurvivalGame.getContentPackTextures().getItem(itemStack.getItem().getId()).getTexture();
			add(new Image(itemTexture)).size(RepresenterUtils.ITEM_WIDTH, RepresenterUtils.ITEM_WIDTH);
			String style = myTotal >= itemStack.getQuantity() ? "white" : "red";
			Label nameLabel = new Label(contentPack.getTranslation(locale, itemStack.getItem(), TranslationKey.NAME) + " ", PixurvivalGame.getSkin(), style);
			add(nameLabel).expand();
			Label quantityLabel = new Label(quantitySB.toString(), PixurvivalGame.getSkin(), style);
			quantityLabel.setAlignment(Align.right);
			add(quantityLabel);
		}
		row();
		// Add item tooltip itself
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
