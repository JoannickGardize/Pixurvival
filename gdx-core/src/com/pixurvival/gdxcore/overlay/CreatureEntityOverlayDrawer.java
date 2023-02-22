package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.livingEntity.CreatureEntity;

public class CreatureEntityOverlayDrawer implements OverlayDrawer<CreatureEntity> {

    private EntityOverlayStackDrawer<CreatureEntity> drawer = new EntityOverlayStackDrawer<>();

    public CreatureEntityOverlayDrawer() {
        drawer.add(new LifeBarDrawer());
    }

    @Override
    public void draw(Batch batch, Viewport worldViewport, CreatureEntity e) {
        drawer.draw(batch, worldViewport, e);
    }
}
