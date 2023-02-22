package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.pixurvival.core.time.DayNightCycleRun;
import com.pixurvival.core.time.Time;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.textures.ColorTextures;

public class DayCycleBar extends Widget {

    private static final Color DAY_COLOR = new Color(1, 1, 0, 1);
    private static final Color NIGHT_COLOR = new Color(0, 0, 0.65f, 1);
    private static final float VERTICAL_MARGIN = 5;
    private static final float HORIZONTAL_MARGIN = 6;

    private float percentDay;
    private Drawable barCursor;

    public DayCycleBar() {
        barCursor = PixurvivalGame.getSkin().getDrawable("bar-cursor");
        Time time = PixurvivalGame.getClient().getWorld().getTime();
        if (time.getDayCycle() instanceof DayNightCycleRun) {
            DayNightCycleRun dayNightCycleRun = (DayNightCycleRun) time.getDayCycle();
            percentDay = (float) dayNightCycleRun.getDayDuration() / (float) dayNightCycleRun.getFullCycleDuration();
        } else {
            percentDay = 1;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Time time = PixurvivalGame.getClient().getWorld().getTime();
        if (time.getDayCycle() instanceof DayNightCycleRun) {
            float width = getWidth() - HORIZONTAL_MARGIN * 2;
            float height = getHeight() - VERTICAL_MARGIN * 2;
            float dayWidth = width * percentDay;
            float x = getX() + getOriginX();
            float y = getY() + getOriginY();
            batch.draw(ColorTextures.get(DAY_COLOR), x + HORIZONTAL_MARGIN, y + VERTICAL_MARGIN, dayWidth, height);
            batch.draw(ColorTextures.get(NIGHT_COLOR), x + HORIZONTAL_MARGIN + dayWidth, y + VERTICAL_MARGIN, width * (1 - percentDay), height);
            DayNightCycleRun dayNightCycleRun = (DayNightCycleRun) time.getDayCycle();
            float cursorPosX = x
                    + width * (dayNightCycleRun.isDay() ? dayNightCycleRun.getCurrentMomentProgess() * percentDay : dayNightCycleRun.getCurrentMomentProgess() * (1 - percentDay) + percentDay);
            batch.setColor(Color.WHITE);
            barCursor.draw(batch, cursorPosX, y, HORIZONTAL_MARGIN * 2, getHeight());
        }
    }

    @Override
    public float getPrefHeight() {
        return 30;
    }

    @Override
    public float getMaxHeight() {
        return getPrefHeight();
    }
}
