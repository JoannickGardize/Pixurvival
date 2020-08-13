package com.pixurvival.core.mapLimits;

import java.util.List;

import com.pixurvival.core.World;
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
			new NextMapLimitAnchorAction(anchors.get(0)).perform(world);
			for (int i = 1; i < anchors.size(); i++) {
				MapLimitsAnchor anchor = anchors.get(i);
				world.getActionTimerManager().addActionTimer(new NextMapLimitAnchorAction(anchor), anchors.get(i - 1).getTime());
			}
		}
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

}
