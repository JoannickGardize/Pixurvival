package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.contentPack.item.Item;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ContentPackAssets;
import com.pixurvival.gdxcore.textures.ItemTexture;
import com.pixurvival.gdxcore.textures.TextureMetrics;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ItemDrawer {

	public static void draw(Batch batch, Item item, float x, float y) {
		ContentPackAssets contentPackTextures = PixurvivalGame.getContentPackTextures();
		ItemTexture itemTexture = contentPackTextures.getItem(item.getId());
		Texture texture = itemTexture.getTexture();
		TextureMetrics metrics = itemTexture.getMetrics();
		draw(batch, contentPackTextures, texture, metrics, x, y);
	}

	public static void draw(Batch batch, ContentPackAssets contentPackTextures, Texture texture, TextureMetrics metrics, float x, float y) {
		batch.draw(texture, (float) (x - metrics.getWorldWidth() / 2 - metrics.getWorldOffsetX() - contentPackTextures.getTruePixelWidth()),
				(float) (y - contentPackTextures.getTruePixelWidth() - metrics.getWorldOffsetY()), (float) (1 + contentPackTextures.getTruePixelWidth() * 2),
				(float) (1 + contentPackTextures.getTruePixelWidth() * 2));
	}
}
