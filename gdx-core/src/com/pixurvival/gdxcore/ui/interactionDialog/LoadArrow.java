package com.pixurvival.gdxcore.ui.interactionDialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.FloatSupplier;

public class LoadArrow extends Actor {

	private FloatSupplier loadSupplier;
	private Texture arrowTexture;

	public LoadArrow(FloatSupplier loadSupplier) {
		this.loadSupplier = loadSupplier;
		arrowTexture = PixurvivalGame.getInstance().getAssetManager().get(PixurvivalGame.FACTORY_ARROW, Texture.class);
		setWidth(arrowTexture.getWidth());
		setHeight(arrowTexture.getHeight());
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		int widthCut = Math.round(MathUtils.clamp(loadSupplier.get(), 0, 1) * arrowTexture.getWidth());
		batch.setColor(Color.YELLOW);
		batch.draw(arrowTexture, getX(), getY(), 0, 0, widthCut, arrowTexture.getHeight());
		batch.setColor(0.5f, .5f, .5f, 1f);
		batch.draw(arrowTexture, getX() + widthCut, getY(), widthCut, 0, arrowTexture.getWidth() - widthCut, arrowTexture.getHeight());
	}
}
