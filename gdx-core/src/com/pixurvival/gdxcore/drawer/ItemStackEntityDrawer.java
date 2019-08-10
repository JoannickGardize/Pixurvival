package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ContentPackTextures;
import com.pixurvival.gdxcore.textures.ItemTexture;
import com.pixurvival.gdxcore.textures.TextureMetrics;

public class ItemStackEntityDrawer extends EntityDrawer<ItemStackEntity> {

	public static final float STACK_OFFSET = 0.1f;

	@Override
	public void drawShadow(Batch batch, ItemStackEntity e) {
		ContentPackTextures contentPackTextures = PixurvivalGame.getContentPackTextures();
		ItemTexture itemTexture = contentPackTextures.getItem(e.getItemStack().getItem().getId());
		Texture shadow = itemTexture.getShadow();
		TextureMetrics metrics = itemTexture.getMetrics();
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.getX() - metrics.getWorldWidth() / 2);
		float y = (float) (drawPosition.getY());
		batch.draw(shadow, x, y - metrics.getWorldWidth() / 4, metrics.getWorldWidth(), metrics.getWorldWidth() / 2);
	}

	@Override
	public void draw(Batch batch, ItemStackEntity e) {
		ContentPackTextures contentPackTextures = PixurvivalGame.getContentPackTextures();
		ItemTexture itemTexture = contentPackTextures.getItem(e.getItemStack().getItem().getId());
		Texture texture = itemTexture.getTexture();
		TextureMetrics metrics = itemTexture.getMetrics();
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.getX() - metrics.getWorldWidth() / 2);
		float y = (float) (drawPosition.getY());
		if (e.getItemStack().getQuantity() == 1) {
			ItemDrawer.draw(batch, contentPackTextures, texture, metrics, x, y);
		} else if (e.getItemStack().getQuantity() == 2) {
			ItemDrawer.draw(batch, contentPackTextures, texture, metrics, x - STACK_OFFSET / 2, y + STACK_OFFSET / 2);
			ItemDrawer.draw(batch, contentPackTextures, texture, metrics, x + STACK_OFFSET / 2, y - STACK_OFFSET / 2);
		} else {
			ItemDrawer.draw(batch, contentPackTextures, texture, metrics, x - STACK_OFFSET, y + STACK_OFFSET);
			ItemDrawer.draw(batch, contentPackTextures, texture, metrics, x, y);
			ItemDrawer.draw(batch, contentPackTextures, texture, metrics, x + STACK_OFFSET, y - STACK_OFFSET);
		}
	}

	@Override
	public void topDraw(Batch batch, ItemStackEntity e) {
	}

}
