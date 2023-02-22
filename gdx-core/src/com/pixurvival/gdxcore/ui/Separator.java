package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.pixurvival.gdxcore.textures.ColorTextures;

public class Separator extends Widget {

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(ColorTextures.get(UIConstants.SEPARATOR_COLOR), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0, 1, 1, false,
                false);
        validate();
    }

    @Override
    public float getPrefHeight() {
        return 3;
    }

    @Override
    public float getMaxHeight() {
        return 3;
    }

    @Override
    public float getMaxWidth() {
        return Float.POSITIVE_INFINITY;
    }
}
