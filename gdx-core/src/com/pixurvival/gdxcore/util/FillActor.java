package com.pixurvival.gdxcore.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.gdxcore.textures.ColorTextures;

public class FillActor extends Actor {

	private Texture colorTexture;

	public FillActor(Color color) {
		colorTexture = ColorTextures.get(color);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.draw(colorTexture, 0, 0, getStage().getWidth(), getStage().getHeight());
	};
}
