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
import com.pixurvival.gdxcore.graphics.ContentPackTextureAnimations;

public class EntitiesActor extends Actor {

	private EntityPool entityPool;
	private Map<Class<? extends Entity>, EntityDrawer<? extends Entity>> drawers = new HashMap<>();

	public EntitiesActor(EntityPool entityPool, ContentPackTextureAnimations contentPackTextureAnimations) {
		this.entityPool = entityPool;
		drawers.put(PlayerEntity.class, new PlayerDrawer(contentPackTextureAnimations.get("character")));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void draw(Batch batch, float parentAlpha) {
		entityPool.foreach(e -> ((EntityDrawer<Entity>) drawers.get(e.getClass())).draw(batch, e));
	}

}
