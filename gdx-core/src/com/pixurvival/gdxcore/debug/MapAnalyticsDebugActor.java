package com.pixurvival.gdxcore.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixurvival.core.map.analytics.Area;
import com.pixurvival.core.map.analytics.MapAnalytics;
import com.pixurvival.core.util.Vector2;

public class MapAnalyticsDebugActor extends Actor {

	public MapAnalyticsDebugActor() {
		setDebug(true);
	}

	@Override
	public void drawDebug(ShapeRenderer shapes) {
		if (MapAnalytics.LAST_ANALYSIS != null) {
			Area area = MapAnalytics.LAST_ANALYSIS.getArea();
			shapes.setColor(Color.GOLD);
			shapes.set(ShapeType.Line);
			shapes.rect(area.getStartX(), area.getStartY(), area.width(), area.height());
			int interval = MapAnalytics.LAST_ANALYSIS.getPointsInterval();
			shapes.set(ShapeType.Filled);
			shapes.setColor(Color.RED);
			for (Vector2 spawns : MapAnalytics.LAST_GAME_AREA_CONFIGURATION.getSpawnSpots()) {
				shapes.circle((float) spawns.getX(), (float) spawns.getY(), 2);
			}
			shapes.setColor(Color.GOLD);
			MapAnalytics.LAST_ANALYSIS.getFreePositions().forEachTruePositions((x, y) -> shapes.circle(x * interval + 0.5f, y * interval + 0.5f, 1));
		}
	}
}
