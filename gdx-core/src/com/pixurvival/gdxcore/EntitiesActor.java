package com.pixurvival.gdxcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.Collidable;
import com.pixurvival.core.Entity;
import com.pixurvival.core.PlayerEntity;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.HarvestableStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.map.Position;
import com.pixurvival.gdxcore.drawer.EntityDrawer;
import com.pixurvival.gdxcore.drawer.MapStructureDrawer;
import com.pixurvival.gdxcore.drawer.PlayerDrawer;

public class EntitiesActor extends Actor {

	private Map<Class<? extends Collidable>, EntityDrawer<? extends Collidable>> drawers = new HashMap<>();
	private List<Collidable> objectsToDraw = new ArrayList<>();

	public EntitiesActor() {
		drawers.put(PlayerEntity.class,
				new PlayerDrawer(PixurvivalGame.getContentPackTextures().getAnimationSet("character")));
		MapStructureDrawer mapStructureDrawer = new MapStructureDrawer(PixurvivalGame.getWorld().getContentPack(),
				PixurvivalGame.getContentPackTextures());
		drawers.put(HarvestableStructure.class, mapStructureDrawer);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void act(float delta) {
		PixurvivalGame.getWorld().getEntityPool()
				.foreach(e -> ((EntityDrawer<Entity>) drawers.get(e.getClass())).update(e));
		super.act(delta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void draw(Batch batch, float parentAlpha) {
		objectsToDraw.clear();
		PixurvivalGame.getWorld().getEntityPool().foreach(e -> objectsToDraw.add(e));
		Vector3 camPos = getStage().getCamera().position;
		OrthographicCamera camera = (OrthographicCamera) getStage().getCamera();
		float width = getStage().getViewport().getWorldWidth() * camera.zoom;
		float height = getStage().getViewport().getWorldHeight() * camera.zoom;
		int startX = (int) Math.floor((camPos.x - width / 2 - 3) / Chunk.CHUNK_SIZE);
		int startY = (int) Math.floor((camPos.y - height / 2 - 3) / Chunk.CHUNK_SIZE);
		int endX = (int) Math.ceil((camPos.x + width / 2 + 3) / Chunk.CHUNK_SIZE);
		int endY = (int) Math.ceil((camPos.y + height / 2 + 3) / Chunk.CHUNK_SIZE);
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				Chunk chunk = PixurvivalGame.getWorld().getMap().chunkAt(new Position(x, y));
				if (chunk == null) {
					continue;
				}
				for (MapStructure s : chunk.getStructures()) {
					objectsToDraw.add(s);
				}
			}
		}

		objectsToDraw.sort((e1, e2) -> (int) ((e2.getY() - e1.getY()) * 10000));
		objectsToDraw.forEach(e -> ((EntityDrawer<Collidable>) drawers.get(e.getClass())).draw(batch, e));

	}

}
