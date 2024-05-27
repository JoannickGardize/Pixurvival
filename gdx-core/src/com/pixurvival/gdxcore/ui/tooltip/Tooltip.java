package com.pixurvival.gdxcore.ui.tooltip;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Tooltip extends Table {

    @Override
    public void act(float delta) {
        toFront();
    }
}
