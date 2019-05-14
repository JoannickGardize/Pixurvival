package com.pixurvival.gdxcore.overlay;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.gdxcore.drawer.DrawData;

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
			infos.getReferencePosition().y += stackElement.draw(batch, infos, e) + OverlaySettings.ENTITY_OVERLAY_MARGIN * infos.getScaleY();
		}
	}

	private void computeValues(Viewport worldViewport, E e) {
		infos.setScaleX(worldViewport.getScreenWidth() / worldViewport.getWorldWidth());
		infos.setScaleY(worldViewport.getScreenHeight() / worldViewport.getWorldHeight());
		DrawData data = (DrawData) e.getCustomData();
		infos.getReferencePosition().x = (float) data.getDrawPosition().getX();
		infos.getReferencePosition().y = (float) data.getDrawPosition().getY() + data.getOverlayOffsetY() + OverlaySettings.ENTITY_OVERLAY_MARGIN;
		worldViewport.project(infos.getReferencePosition());
	}
}