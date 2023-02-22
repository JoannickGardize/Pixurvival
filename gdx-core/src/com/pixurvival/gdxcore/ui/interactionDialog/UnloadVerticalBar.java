package com.pixurvival.gdxcore.ui.interactionDialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.util.FloatSupplier;

public class UnloadVerticalBar extends Actor {

    private static final float SCALE = 2;
    private static final Color UNLOAD_COLOR = new Color(.5f, .5f, .5f, 1f);
    private static final Color LOAD_COLOR = Color.YELLOW;

    private FloatSupplier loadSupplier;
    private Texture barTexture;

    public UnloadVerticalBar(FloatSupplier loadSupplier) {
        this.loadSupplier = loadSupplier;
        barTexture = PixurvivalGame.getInstance().getAssetManager().get(PixurvivalGame.FUEL_BAR, Texture.class);
        setWidth(barTexture.getWidth() * SCALE);
        setHeight(barTexture.getHeight() * SCALE);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float load = MathUtils.clamp(loadSupplier.get(), 0, 1);
        int srcHeightCut = Math.round(load * barTexture.getHeight());
        float heightCut = srcHeightCut * getHeight() / barTexture.getHeight();
        srcHeightCut = barTexture.getHeight() - srcHeightCut;
        batch.setColor(LOAD_COLOR);
        batch.draw(barTexture, getX(), getY(), 0, 0, getWidth(), heightCut, 1, 1, 0, 0, srcHeightCut, barTexture.getWidth(), barTexture.getHeight() - srcHeightCut, false, false);
        batch.setColor(UNLOAD_COLOR);
        batch.draw(barTexture, getX(), getY() + heightCut, 0, 0, getWidth(), getHeight() - heightCut, 1, 1, 0, 0, 0, barTexture.getWidth(), srcHeightCut, false, false);
    }
}
