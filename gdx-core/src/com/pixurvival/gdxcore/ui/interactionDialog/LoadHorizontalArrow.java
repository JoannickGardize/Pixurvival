package com.pixurvival.gdxcore.ui.interactionDialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.FloatSupplier;

public class LoadHorizontalArrow extends Actor {

    private static final float SCALE = 2;
    private static final Color UNLOAD_COLOR = new Color(.5f, .5f, .5f, 1f);
    private static final Color LOAD_COLOR = Color.YELLOW;

    private FloatSupplier loadSupplier;
    private Texture arrowTexture;

    public LoadHorizontalArrow(FloatSupplier loadSupplier) {
        this.loadSupplier = loadSupplier;
        arrowTexture = PixurvivalGame.getInstance().getAssetManager().get(PixurvivalGame.FACTORY_ARROW, Texture.class);
        setWidth(arrowTexture.getWidth() * SCALE);
        setHeight(arrowTexture.getHeight() * SCALE);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float load = MathUtils.clamp(loadSupplier.get(), 0, 1);
        int srcWidthCut = Math.round(load * arrowTexture.getWidth());
        float widthCut = srcWidthCut * getWidth() / arrowTexture.getWidth();
        batch.setColor(LOAD_COLOR);
        batch.draw(arrowTexture, getX(), getY(), 0, 0, widthCut, getHeight(), 1, 1, 0, 0, 0, srcWidthCut, arrowTexture.getHeight(), false, false);
        batch.setColor(UNLOAD_COLOR);
        batch.draw(arrowTexture, getX() + widthCut, getY(), 0, 0, getWidth() - widthCut, getHeight(), 1, 1, 0, srcWidthCut, 0, arrowTexture.getWidth() - srcWidthCut, arrowTexture.getHeight(), false,
                false);
    }
}
