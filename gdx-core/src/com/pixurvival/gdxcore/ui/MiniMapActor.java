package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.World;
import com.pixurvival.core.util.ByteArray2D;
import com.pixurvival.gdxcore.graphics.ContentPackTextures;

public class MiniMapActor extends Actor {

	Texture mapTexture;

	public MiniMapActor(World world, ContentPackTextures contentPackTextures) {
		ByteArray2D data = world.getMap().getData();
		Pixmap pixmap = new Pixmap(data.getWidth(), data.getHeight(), Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				pixmap.drawPixel(x, pixmap.getHeight() - 1 - y, contentPackTextures.getTileColor(data.get(x, y)));
			}
		}
		mapTexture = new Texture(pixmap);
		mapTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(mapTexture, getX(), getY(), getWidth(), getHeight());
	}
}
