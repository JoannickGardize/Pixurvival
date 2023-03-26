package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.overlay.OverlayConstants;
import com.pixurvival.gdxcore.util.DrawUtils;
import com.pixurvival.gdxcore.util.FloatSupplier;

public class ValueBarActor extends Actor {

    private FloatSupplier valueSupplier;
    private FloatSupplier maxSupplier;
    private GlyphLayout titleLayout;
    private GlyphLayout percentLayout = new GlyphLayout();
    private Rectangle tmpRect = new Rectangle();

    public ValueBarActor(FloatSupplier valueSupplier, FloatSupplier maxSupplier, String titleKey, Color color) {
        this.valueSupplier = valueSupplier;
        this.maxSupplier = maxSupplier;
        titleLayout = new GlyphLayout(PixurvivalGame.getOverlayFont(), PixurvivalGame.getString(titleKey));
        setColor(color);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(1, 1, 1, parentAlpha);
        tmpRect.set(getX(), getY(), getWidth(), getHeight());
        float value = valueSupplier.get();
        float max = maxSupplier.get();
        DrawUtils.drawPercentBar(batch, tmpRect, value / max, getColor());
        PixurvivalGame.getOverlayFont().draw(batch, titleLayout,
                getX() + OverlayConstants.BAR_BORDER_SIZE + 2, getY() + getHeight() / 2 + titleLayout.height / 2);
        percentLayout.setText(PixurvivalGame.getOverlayFont(), MathUtils.round(value) + "/" + MathUtils.round(max));
        PixurvivalGame.getOverlayFont().draw(batch, percentLayout,
                getX() + getWidth() - OverlayConstants.BAR_BORDER_SIZE - 2 - percentLayout.width,
                getY() + getHeight() / 2 + percentLayout.height / 2);
    }
}
