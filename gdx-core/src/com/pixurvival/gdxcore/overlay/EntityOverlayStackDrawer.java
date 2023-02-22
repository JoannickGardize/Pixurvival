package com.pixurvival.gdxcore.overlay;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.gdxcore.drawer.DrawData;

import java.util.ArrayList;
import java.util.List;

public class EntityOverlayStackDrawer<E extends Entity> implements OverlayDrawer<E> {

    private List<OverlayStackElementDrawer<? super E>> stack = new ArrayList<>();
    private OverlayInfos infos = new OverlayInfos();

    public void add(OverlayStackElementDrawer<? super E> element) {
        stack.add(element);
    }

    @Override
    public void draw(Batch batch, Viewport worldViewport, E e) {
        computeValues(worldViewport, e);
        for (OverlayStackElementDrawer<? super E> stackElement : stack) {
            float height = stackElement.draw(batch, infos, e);
            if (height != 0) {
                infos.getReferencePosition().y += height + OverlayConstants.ENTITY_OVERLAY_MARGIN;
            }
        }
    }

    private void computeValues(Viewport worldViewport, E e) {
        DrawData data = (DrawData) e.getCustomData();
        infos.getReferencePosition().x = data.getDrawPosition().getX();
        infos.getReferencePosition().y = data.getDrawPosition().getY() + data.getOverlayOffsetY();
        worldViewport.project(infos.getReferencePosition());
        infos.getReferencePosition().y += OverlayConstants.ENTITY_OVERLAY_MARGIN;
    }
}