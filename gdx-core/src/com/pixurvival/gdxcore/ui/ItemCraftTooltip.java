package com.pixurvival.gdxcore.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.pixurvival.core.item.Inventory;
import com.pixurvival.core.item.InventoryListener;
import com.pixurvival.core.item.ItemCraft;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.Caches;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ItemCraftTooltip extends Actor implements InventoryListener {

	private @Getter static ItemCraftTooltip instance = new ItemCraftTooltip();

	public static final float ITEM_WIDTH = 20;
	public static final float MARGIN = 5;
	public static final float OFFSET = 10;

	@AllArgsConstructor
	private static class Line {
		Texture itemTexture;
		GlyphLayout textGlyph;
	}

	private ItemCraft itemCraft;
	private List<Line> lines = new ArrayList<>();
	private Vector2 tmpVec = new Vector2();

	private ItemCraftTooltip() {
		this.setTouchable(Touchable.disabled);
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
		tmpVec.set(Gdx.input.getX(), Gdx.input.getY());
		getStage().getViewport().unproject(tmpVec);
		setX(tmpVec.x + OFFSET);
		setY(tmpVec.y + OFFSET);
		PixurvivalGame.getSkin().get(TextFieldStyle.class).background.draw(batch, getX(), getY(), getWidth(),
				getHeight());
		float y = getY() + getHeight() - MARGIN - ITEM_WIDTH;
		for (Line line : lines) {
			batch.draw(line.itemTexture, getX() + MARGIN, y, ITEM_WIDTH, ITEM_WIDTH);
			PixurvivalGame.getSkin().getFont("default").draw(batch, line.textGlyph, getX() + MARGIN * 2 + ITEM_WIDTH,
					y + line.textGlyph.height + (ITEM_WIDTH - line.textGlyph.height) / 2);
			y -= MARGIN + ITEM_WIDTH;
		}
	}

	private void build() {
		Inventory inv = PixurvivalGame.getClient().getMyInventory();
		float maxWidth = 0;
		lines.clear();
		for (ItemStack itemStack : itemCraft.getRecipes()) {
			int myTotal = inv.totalOf(itemStack.getItem());
			String quantityString = myTotal + " / " + itemStack.getQuantity();
			GlyphLayout glyphLayout = null;
			if (myTotal >= itemStack.getQuantity()) {
				glyphLayout = Caches.whiteGlyphLayout.get(quantityString);
			} else {
				glyphLayout = Caches.redGlyphLayout.get(quantityString);
			}
			Texture itemTexture = PixurvivalGame.getContentPackTextures().getItem(itemStack.getItem().getId())
					.getTexture();
			lines.add(new Line(itemTexture, glyphLayout));
			if (glyphLayout.width > maxWidth) {
				maxWidth = glyphLayout.width;
			}
		}
		setWidth(maxWidth + MARGIN * 3 + ITEM_WIDTH);
		setHeight(itemCraft.getRecipes().size() * (ITEM_WIDTH + MARGIN) + MARGIN);
	}

	@Override
	public void slotChanged(Inventory inventory, int slotIndex, ItemStack previousItemStack, ItemStack newItemStack) {
		if (isVisible()) {
			build();
		}
	}
}