package com.pixurvival.gdxcore.ui.tooltip;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Tooltip extends Table {

    @Override
    public void draw(Batch batch, float parentAlpha) {
        toFront();
        super.draw(batch, parentAlpha);
    }
}
