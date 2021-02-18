package com.pixurvival.gdxcore;

import java.util.ArrayList;
import java.util.IdentityHashMap;
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
import com.pixurvival.core.ActionPreconditions;
import com.pixurvival.core.Body;
import com.pixurvival.core.GameConstants;
import com.pixurvival.core.World.Type;
import com.pixurvival.core.contentPack.item.StructureItem;
import com.pixurvival.core.contentPack.structure.Structure;
import com.pixurvival.core.entity.EffectEntity;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.item.ItemStack;
import com.pixurvival.core.item.ItemStackEntity;
import com.pixurvival.core.livingEntity.CreatureEntity;
import com.pixurvival.core.livingEntity.PlayerEntity;
import com.pixurvival.core.map.FactoryStructureEntity;
import com.pixurvival.core.map.HarvestableStructureEntity;
import com.pixurvival.core.map.InventoryStructureEntity;
import com.pixurvival.core.map.StructureEntity;
import com.pixurvival.core.util.MathUtils;
import com.pixurvival.gdxcore.drawer.CreatureDrawer;
import com.pixurvival.gdxcore.drawer.EffectDrawer;
import com.pixurvival.gdxcore.drawer.ElementDrawer;
import com.pixurvival.gdxcore.drawer.FactoryStructureEntityDrawer;
import com.pixurvival.gdxcore.drawer.GhostStructureDrawer;
import com.pixurvival.gdxcore.drawer.HarvestableStructureEntityDrawer;
import com.pixurvival.gdxcore.drawer.InventoryStructureEntityDrawer;
import com.pixurvival.gdxcore.drawer.ItemStackEntityDrawer;
import com.pixurvival.gdxcore.drawer.PlayerDrawer;
import com.pixurvival.gdxcore.drawer.StructureEntityDrawer;
import com.pixurvival.gdxcore.util.DrawUtils;

public class EntitiesActor extends Actor {

	private Map<Class<? extends Body>, ElementDrawer<? extends Body>> drawers = new IdentityHashMap<>();
	private List<Body> objectsToDraw = new ArrayList<>();
	private List<Body> allObjectsToDraw = new ArrayList<>();
	private int actualY;

	public EntitiesActor() {
		drawers.put(PlayerEntity.class, new PlayerDrawer());
		drawers.put(HarvestableStructureEntity.class, new HarvestableStructureEntityDrawer());
		drawers.put(StructureEntity.class, new StructureEntityDrawer());
		drawers.put(InventoryStructureEntity.class, new InventoryStructureEntityDrawer());
		drawers.put(FactoryStructureEntity.class, new FactoryStructureEntityDrawer());
		drawers.put(GhostStructure.class, new GhostStructureDrawer());
		drawers.put(ItemStackEntity.class, new ItemStackEntityDrawer());
		drawers.put(CreatureEntity.class, new CreatureDrawer());
		drawers.put(EffectEntity.class, new EffectDrawer());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void draw(Batch batch, float parentAlpha) {
		ensureMyPlayerInScreen();
		allObjectsToDraw.clear();
		actualY = Integer.MAX_VALUE;
		// TODO Change this 3 by a smart value
		DrawUtils.foreachChunksInScreen(getStage(), 3, chunk -> {
			if (actualY != chunk.getPosition().getY() && !objectsToDraw.isEmpty()) {
				flushEntityRow();
			}
			actualY = chunk.getPosition().getY();
			chunk.getEntities().foreach((group, map) -> {
				ElementDrawer<Entity> drawer = (ElementDrawer<Entity>) drawers.get(group.getType());
				map.values().forEach(e -> {
					drawer.update(e);
					objectsToDraw.add(e);
				});
			});
			chunk.forEachStructure(objectsToDraw::add);
			// TODO cache objectsToDraw?
		});
		flushEntityRow();
		allObjectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).backgroundDraw(batch, e));
		allObjectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).drawShadow(batch, e));
		allObjectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).draw(batch, e));
		allObjectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).frontDraw(batch, e));

		drawGhostStructure(batch);
	}

	private void flushEntityRow() {
		objectsToDraw.sort((e1, e2) -> Float.compare(e2.getDisplayDeath(), e1.getDisplayDeath()));
		allObjectsToDraw.addAll(objectsToDraw);
		objectsToDraw.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drawDebug(ShapeRenderer shapes) {
		drawChunksBorders(shapes);
		allObjectsToDraw.forEach(e -> ((ElementDrawer<Body>) drawers.get(e.getClass())).drawDebug(shapes, e));
	}

	private void drawChunksBorders(ShapeRenderer shapes) {
		Vector3 camPos = getStage().getCamera().position;
		OrthographicCamera camera = (OrthographicCamera) getStage().getCamera();
		float width = getStage().getViewport().getWorldWidth() * camera.zoom;
		float height = getStage().getViewport().getWorldHeight() * camera.zoom;
		int startX = MathUtils.floor((camPos.x - width / 2 - 3) / GameConstants.CHUNK_SIZE);
		int startY = MathUtils.floor((camPos.y - height / 2 - 3) / GameConstants.CHUNK_SIZE);
		int endX = MathUtils.floor((camPos.x + width / 2 + 3) / GameConstants.CHUNK_SIZE);
		int endY = MathUtils.floor((camPos.y + height / 2 + 3) / GameConstants.CHUNK_SIZE);
		shapes.setColor(Color.GRAY);
		for (int x = startX; x <= endX; x++) {
			for (int y = startY; y <= endY; y++) {
				shapes.rect(x * GameConstants.CHUNK_SIZE, y * GameConstants.CHUNK_SIZE, GameConstants.CHUNK_SIZE, GameConstants.CHUNK_SIZE);
			}
		}
		if (PixurvivalGame.getWorld().getType() == Type.CLIENT) {
			shapes.setColor(Color.YELLOW);
			PlayerEntity player = PixurvivalGame.getClient().getMyPlayer();
			if (player != null) {
				shapes.rect(player.getPosition().getX() - GameConstants.PLAYER_VIEW_DISTANCE, player.getPosition().getY() - GameConstants.PLAYER_VIEW_DISTANCE, GameConstants.PLAYER_VIEW_DISTANCE * 2,
						GameConstants.PLAYER_VIEW_DISTANCE * 2);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void drawGhostStructure(Batch batch) {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		ItemStack heldItemStack = PixurvivalGame.getClient().getMyInventory().getHeldItemStack();
		if (heldItemStack != null && heldItemStack.getItem() instanceof StructureItem) {
			Vector2 mousePos = getStage().getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
			int x = MathUtils.floor(mousePos.x);
			int y = MathUtils.floor(mousePos.y);
			Structure structure = ((StructureItem) heldItemStack.getItem()).getStructure();
			boolean valid = ActionPreconditions.canPlace(myPlayer, structure, x, y);
			GhostStructure ghostStructure = new GhostStructure(structure, x, y, valid);
			ElementDrawer<Body> drawer = ((ElementDrawer<Body>) drawers.get(ghostStructure.getClass()));
			drawer.backgroundDraw(batch, ghostStructure);
			drawer.drawShadow(batch, ghostStructure);
			drawer.draw(batch, ghostStructure);
			drawer.frontDraw(batch, ghostStructure);
		}
	}

	private void ensureMyPlayerInScreen() {
		PlayerEntity myPlayer = PixurvivalGame.getClient().getMyPlayer();
		if (!DrawUtils.isInsideScreen(getStage(), myPlayer.getPosition())) {
			Vector3 camPos = getStage().getCamera().position;
			camPos.x = myPlayer.getPosition().getX();
			camPos.y = myPlayer.getPosition().getY();
		}
	}
}
