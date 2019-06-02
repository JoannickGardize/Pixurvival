package com.pixurvival.gdxcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.Body;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.Chunk;
import com.pixurvival.core.map.ChunkPosition;
import com.pixurvival.core.map.ShortLivedMapStructure;
import com.pixurvival.core.map.HarvestableMapStructure;
import com.pixurvival.core.map.MapStructure;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.drawer.CreatureDrawer;
import com.pixurvival.gdxcore.drawer.EffectDrawer;
import com.pixurvival.gdxcore.drawer.ElementDrawer;
import com.pixurvival.gdxcore.drawer.GhostStructureDrawer;
import com.pixurvival.gdxcore.drawer.ItemStackDrawer;
import com.pixurvival.gdxcore.drawer.MapStructureDrawer;
import com.pixurvival.gdxcore.drawer.PlayerDrawer;

public class EntitiesActor extends Actor {

	private Map<Class<? extends Body>, ElementDrawer<? extends Body>> drawers = new HashMap<>();
	private List<Body> objectsToDraw = new ArrayList<>();

	public EntitiesActor() {
		drawers.put(PlayerEntity.class, new PlayerDrawer());
		MapStructureDrawer mapStructureDrawer = new MapStructureDrawer();
		drawers.put(HarvestableMapStructure.class, mapStructureDrawer);
		drawers.put(ShortLivedMapStructure.class, mapStructureDrawer);
		drawers.put(GhostStructure.class, new GhostStructureDrawer());
		drawers.put(ItemStackEntity.class, new ItemStackDrawer());
		drawers.put(CreatureEntity.class, new CreatureDrawer());
		drawers.put(EffectEntity.class, new EffectDrawer());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void act(float delta) {
		PixurvivalGame.getWorld().getEntityPool().foreach(e -> ((ElementDrawer<Entity>) drawers.get(e.getClass())).update(e));
		super.act(delta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void draw(Batch batch, float parentAlpha) {
		objectsToDraw.clear();
		// TODO utiliser le pool des chunks
		PixurvivalGame.getWorld().getEntityPool().foreach(e -> objectsToDraw.add(e));
		Vector3 camPos = getStage().getCamera().position;
		OrthographicCamera camera = (OrthographicCamera) getStage().getCamera();
		float width = getStage().getViewport().getWorldWidth() * camera.zoom;
		float height = getStage().getViewport().getWorldHeight() * camera.zoom;
		int startX = MathUtils.floor((camPos.x - width / 2 - 3) / GameConstants.CHUNK_SIZE);
		int startY = MathUtils.floor((camPos.y - height / 2 - 3) / GameConstants.CHUNK_SIZE);
		int endX = MathUtils.ceil((camPos.x + width / 2 + 3) / GameConstants.CHUNK_SIZE);
		int endY = MathUtils.ceil((camPos.y + height / 2 + 3) / GameConstants.CHUNK_SIZE);
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				Chunk chunk = PixurvivalGame.getWorld().getMap().chunkAt(new ChunkPosition(x, y));
				if (chunk == null) {
					continue;
				}
				chunk.forEachStructure(objectsToDraw::add);
			}
		}
		objectsToDraw.sort((e1, e2) -> (int) ((e2.getY() - e1.getY()) * 10000));

		manageGhostStructure();
		objectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).drawShadow(batch, e));
		objectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).draw(batch, e));
		objectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).topDraw(batch, e));

	}

	@SuppressWarnings("unchecked")
	@Override
	public void drawDebug(ShapeRenderer shapes) {
		drawChunksBorders(shapes);
		objectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).drawDebug(shapes, e));
	}

	private void drawChunksBorders(ShapeRenderer shapes) {
		Vector3 camPos = getStage().getCamera().position;
		OrthographicCamera camera = (OrthographicCamera) getStage().getCamera();
		float width = getStage().getViewport().getWorldWidth() * camera.zoom;
		float height = getStage().getViewport().getWorldHeight() * camera.zoom;
		int startX = MathUtils.floor((camPos.x - width / 2 - 3) / GameConstants.CHUNK_SIZE);
		int startY = MathUtils.floor((camPos.y - height / 2 - 3) / GameConstants.CHUNK_SIZE);
		int endX = MathUtils.ceil((camPos.x + width / 2 + 3) / GameConstants.CHUNK_SIZE);
		int endY = MathUtils.ceil((camPos.y + height / 2 + 3) / GameConstants.CHUNK_SIZE);
		shapes.setColor(Color.GRAY);
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				shapes.rect(x * GameConstants.CHUNK_SIZE, y * GameConstants.CHUNK_SIZE, GameConstants.CHUNK_SIZE, GameConstants.CHUNK_SIZE);
			}
		}
	}

	private void manageGhostStructure() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (myPlayer == null) {
			return;
		}
		ItemStack heldItemStack = PixurvivalGame.getClient().getMyInventory().getHeldItemStack();
		if (heldItemStack != null && heldItemStack.getItem() instanceof StructureItem) {
			Vector2 mousePos = getStage().getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			int x = MathUtils.floor(mousePos.x);
			int y = MathUtils.floor(mousePos.y);
			Structure structure = ((StructureItem) heldItemStack.getItem()).getStructure();
			boolean valid = MapStructure.canPlace(myPlayer, myPlayer.getWorld().getMap(), structure, x, y);
			GhostStructure ghostStructure = new GhostStructure(structure, x, y, valid);
			objectsToDraw.add(ghostStructure);
		}
	}
}
