package com.pixurvival.gdxcore;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.Entity;
import com.pixurvival.core.EntityPool;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.gdxcore.drawer.EntityDrawer;
import com.pixurvival.gdxcore.drawer.PlayerDrawer;

public class EntitiesActor extends Actor {

	private EntityPool entityPool;
	private Map<Class<? extends Entity>, EntityDrawer<? extends Entity>> drawers = new HashMap<>();

	public EntitiesActor(EntityPool entityPool) {
		this.entityPool = entityPool;
		drawers.put(PlayerEntity.class,
				new PlayerDrawer(PixurvivalGame.getContentPackTextures().getAnimationSet("character")));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void act(float delta) {
		entityPool.foreach(e -> ((EntityDrawer<Entity>) drawers.get(e.getClass())).update(e));
		super.act(delta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void draw(Batch batch, float parentAlpha) {
		entityPool.foreach(e -> ((EntityDrawer<Entity>) drawers.get(e.getClass())).draw(batch, e));
	}

}
