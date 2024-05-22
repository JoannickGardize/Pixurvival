package com.pixurvival.gdxcore.ui;

import com.badlogic.gdx.graphics.g2d.Batch;

public class MiniMapUI extends UIWindow {

    private MiniMapWidget mapWidget;

    public MiniMapUI() {
        super("miniMap");
        mapWidget = new MiniMapWidget();
        add(mapWidget).grow();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        /*mapActor.setPosition(0, 0);
        mapActor.setSize(getWidth(), getHeight());*/
        super.draw(batch, parentAlpha);
    }

    public void dispose() {
        mapWidget.dispose();
    }
}
