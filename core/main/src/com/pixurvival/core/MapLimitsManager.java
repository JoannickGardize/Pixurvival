package com.pixurvival.core;

import java.util.List;

import com.pixurvival.core.contentPack.gameMode.MapLimits;
import com.pixurvival.core.contentPack.gameMode.MapLimitsAnchor;
import com.pixurvival.core.entity.Entity;
import com.pixurvival.core.entity.EntityGroup;
import com.pixurvival.core.livingEntity.LivingEntity;
import com.pixurvival.core.util.Plugin;
import com.pixurvival.core.util.Rectangle;
import com.pixurvival.core.util.Vector2;

public class MapLimitsManager implements Plugin<World> {

	public void initialize(World world, Vector2 spawnCenter) {
		MapLimits mapLimits = world.getGameMode().getMapLimits();
		MapLimitsRun mapLimitsRun = new MapLimitsRun();
		world.setMapLimitsRun(mapLimitsRun);
		mapLimitsRun.setRectangle(new Rectangle(spawnCenter, mapLimits.getInitialSize()));
		mapLimitsRun.setTrueDamagePerSecond(mapLimits.getInitialDamagePerSecond());
		mapLimitsRun.setTrueDamagePerSecond(mapLimits.getInitialDamagePerSecond());
		Rectangle rectangle = new Rectangle(world.getSpawnCenter(), mapLimits.getInitialSize());
		mapLimitsRun.setRectangle(rectangle);
		MapLimitsAnchorRun initialAnchorRun = new MapLimitsAnchorRun();
		initialAnchorRun.setDamagePerSecond(mapLimits.getInitialDamagePerSecond());
		initialAnchorRun.setRectangle(rectangle);
		initialAnchorRun.setTime(0);
		mapLimitsRun.setFrom(initialAnchorRun);
		List<MapLimitsAnchor> anchors = mapLimits.getAnchors();
		mapLimitsRun.setTo(initialAnchorRun);
		if (!anchors.isEmpty()) {
			nextAnchor(world, anchors.get(0));
			for (int i = 1; i < anchors.size(); i++) {
				MapLimitsAnchor anchor = anchors.get(i);
				world.getActionTimerManager().addActionTimer(() -> nextAnchor(world, anchor), anchors.get(i - 1).getTime());
			}
		}
	}

	public void nextAnchor(World world, MapLimitsAnchor anchor) {
		MapLimitsRun mapLimits = world.getMapLimitsRun();
		mapLimits.setFrom(mapLimits.getTo());
		MapLimitsAnchorRun anchorRun = new MapLimitsAnchorRun();
		mapLimits.setTo(anchorRun);
		anchorRun.setTime(anchor.getTime());
		anchorRun.setDamagePerSecond(anchor.getDamagePerSecond());
		anchorRun.setRectangle(buildNextRectangle(world, anchor));
	}

	@Override
	public void update(World world) {
		MapLimitsRun mapLimits = world.getMapLimitsRun();
		Rectangle rectangle = mapLimits.getRectangle();
		MapLimitsAnchorRun from = mapLimits.getFrom();
		MapLimitsAnchorRun to = mapLimits.getTo();
		long diffTime = to.getTime() - from.getTime();
		if (diffTime > 0) {
			long time = world.getTime().getTimeMillis();
			float alpha = Math.min(1, (float) ((double) (time - from.getTime()) / (double) diffTime));
			Rectangle fromRect = from.getRectangle();
			Rectangle toRect = to.getRectangle();
			rectangle.setStartX(fromRect.getStartX() + (toRect.getStartX() - fromRect.getStartX()) * alpha);
			rectangle.setStartY(fromRect.getStartY() + (toRect.getStartY() - fromRect.getStartY()) * alpha);
			rectangle.setEndX(fromRect.getEndX() + (toRect.getEndX() - fromRect.getEndX()) * alpha);
			rectangle.setEndY(fromRect.getEndY() + (toRect.getEndY() - fromRect.getEndY()) * alpha);
			mapLimits.setTrueDamagePerSecond(from.getDamagePerSecond() + (to.getDamagePerSecond() - from.getDamagePerSecond()) * alpha);
		}

		for (Entity e : world.getEntityPool().get(EntityGroup.PLAYER)) {
			if (!rectangle.contains(e.getPosition())) {
				((LivingEntity) e).takeTrueDamage(mapLimits.getTrueDamagePerSecond() * world.getTime().getDeltaTime());
			}
		}
	}

	private Rectangle buildNextRectangle(World world, MapLimitsAnchor anchor) {
		MapLimitsRun mapLimits = world.getMapLimitsRun();
		if (world.getGameMode().getMapLimits().isShrinkRandomly()) {
			Rectangle rectangle = mapLimits.getFrom().getRectangle();
			float size = anchor.getSize();
			float widthDiff = rectangle.getWidth() - size;
			float heightDiff = rectangle.getHeight() - size;
			float startX = rectangle.getStartX() + world.getRandom().nextFloat() * widthDiff;
			float startY = rectangle.getStartY() + world.getRandom().nextFloat() * heightDiff;
			return new Rectangle(startX, startY, startX + size, startY + size);
		} else {
			return new Rectangle(mapLimits.getFrom().getRectangle().getCenter(), anchor.getSize());
		}
	}
}
