package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.pixurvival.gdxcore.textures.ColorTextures;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Separator extends Widget {

    private final Color color;

    public Separator() {
        color = UIConstants.SEPARATOR_COLOR;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(ColorTextures.get(color), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0, 1, 1, false,
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
