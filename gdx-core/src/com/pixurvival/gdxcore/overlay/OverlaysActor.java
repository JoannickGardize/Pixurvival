package com.pixurvival.gdxcore.overlay;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.entity.EntityPool;
import com.pixurvival.gdxcore.PixurvivalGame;
import com.pixurvival.gdxcore.ScreenResizeEvent;

public class OverlaysActor extends Actor implements EventListener {

	private Map<EntityGroup, OverlayDrawer<? extends Entity>> entityOverlayDrawers = new EnumMap<>(EntityGroup.class);
	private Viewport worldViewport;

	private Rectangle scissors = new Rectangle();

	public OverlaysActor(Viewport worldViewport) {
		this.worldViewport = worldViewport;
		entityOverlayDrawers.put(EntityGroup.PLAYER, new PlayerEntityOverlayDrawer());
		// entityOverlayDrawers.put(EntityGroup.CREATURE, new
		// LivingEntityOverlayDrawer<LivingEntity>());
	}

	@Override
	public void act(float delta) {
		// TODO Auto-generated method stub
		super.act(delta);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void draw(Batch batch, float parentAlpha) {
		ScissorStack.pushScissors(scissors);
		batch.setColor(1, 1, 1, 0.75f);
		EntityPool entityPool = PixurvivalGame.getWorld().getEntityPool();
		entityOverlayDrawers.forEach((group, drawer) -> entityPool.get(group).forEach(e -> ((EntityOverlayStackDrawer) drawer).draw(batch, worldViewport, e)));
		batch.flush(); // Make sure nothing is clipped before we want it to.
		ScissorStack.popScissors();
	}

	@Override
	public boolean handle(Event event) {
		if (event instanceof ScreenResizeEvent && getParent() == getStage().getRoot()) {
			ScreenResizeEvent screenResizeEvent = (ScreenResizeEvent) event;
			int width = screenResizeEvent.getNewScreenWidth();
			int height = screenResizeEvent.getNewScreenHeight();
			if (width > height) {
				int gutterSize = (width - height) / 2;
				scissors.setX(gutterSize);
				scissors.setY(0);
				scissors.setWidth(height);
				scissors.setHeight(height);
			} else {
				int gutterSize = (height - width) / 2;
				scissors.setX(0);
				scissors.setY(gutterSize);
				scissors.setWidth(width);
				scissors.setHeight(width);
			}
		}
		return false;
	}
}
