package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;

public class MiniMapUI extends UIWindow {

    private MiniMapWidget mapWidget;

    public MiniMapUI() {
        super("miniMap");
        mapWidget = new MiniMapWidget();
        add(mapWidget).grow();
        pack();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        mapWidget.dispose();
    }
}
