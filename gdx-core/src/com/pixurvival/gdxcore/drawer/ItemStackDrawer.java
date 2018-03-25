package com.pixurvival.gdxcore.drawer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.util.Vector2;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ContentPackTextures;
import com.pixurvival.gdxcore.textures.ItemTexture;
import com.pixurvival.gdxcore.textures.TextureMetrics;

public class ItemStackDrawer extends EntityDrawer<ItemStackEntity> {

	@Override
	public void drawShadow(Batch batch, ItemStackEntity e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Batch batch, ItemStackEntity e) {
		ContentPackTextures contentPackTextures = PixurvivalGame.getContentPackTextures();
		ItemTexture itemTexture = contentPackTextures.getItem(e.getItemStack().getItem().getId());
		Texture shadow = itemTexture.getShadow();
		Texture texture = itemTexture.getTexture();
		TextureMetrics metrics = itemTexture.getMetrics();
		DrawData data = (DrawData) e.getCustomData();
		Vector2 drawPosition = data.getDrawPosition();
		float x = (float) (drawPosition.x - metrics.getWorldWidth() / 2);
		float y = (float) ((drawPosition.y));
		batch.draw(shadow, x, y - metrics.getWorldWidth() / 4, metrics.getWorldWidth(), metrics.getWorldWidth() / 2);
		batch.draw(texture, (float) (x - metrics.getWorldOffsetX() - contentPackTextures.getTruePixelWidth()),
				(float) (y - contentPackTextures.getTruePixelWidth() - metrics.getWorldOffsetY()), 1, 1);
	}

	@Override
	public void topDraw(Batch batch, ItemStackEntity e) {
		// TODO Auto-generated method stub

	}

}
