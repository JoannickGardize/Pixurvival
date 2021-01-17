package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.gdxcore.PixurvivalGame;

public class MouseIconActor extends Actor {
	private Texture iconTexture;

	public MouseIconActor() {
		iconTexture = PixurvivalGame.getInstance().getAssetManager().get(PixurvivalGame.RIGHT_CLICK_ICON, Texture.class);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, 0.7f);
		setPosition(Gdx.input.getX(), Gdx.input.getY());
		batch.draw(iconTexture, getX() + 15, Gdx.graphics.getHeight() - getY() - iconTexture.getHeight() - 5);
	}
}
