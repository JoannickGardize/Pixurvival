package com.pixurvival.core.map.analytics;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.function.Consumer;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.map.TiledMap;
import com.pixurvival.core.util.Vector2;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapAnalytics {

	public static AreaAnalysisResult LAST_ANALYSIS = null;

	public static GameAreaConfiguration LAST_GAME_AREA_CONFIGURATION = null;

	private static final int MAX_FAIL_POSITION_CHECK = 5;
	private static final int POINTS_INTERVAL = 16;
	private static final int MAX_START_POINT_TRY = 15;

	private @NonNull Random random;

	public GameAreaConfiguration buildGameAreaConfiguration(TiledMap map, AreaSearchCriteria criteria) throws MapAnalyticsException {
		AreaAnalysisResult areaAnalysisResult = findArea(new TiledMapCursor(map), criteria);
		if (areaAnalysisResult == null) {
			throw new MapAnalyticsException();
		}
		Vector2[] spawnSpots;
		if (criteria.getNumberOfSpawnSpots() == 1) {
			Position position = areaAnalysisResult.getFreePositions().getMostCenteredPoint();
			spawnSpots = new Vector2[] { position.toVector2(areaAnalysisResult.getPointsInterval()) };
		} else {
			Position[] positions = areaAnalysisResult.getFreePositions().getExternalPoints();
			spawnSpots = new Vector2[criteria.getNumberOfSpawnSpots()];
			int spawnGap = positions.length / criteria.getNumberOfSpawnSpots();
			int offset = random.nextInt(positions.length);
			for (int i = 0; i < criteria.getNumberOfSpawnSpots(); i++) {
				Position position = positions[(offset + spawnGap * i) % positions.length];
				spawnSpots[i] = position.toVector2(areaAnalysisResult.getPointsInterval());
			}
		}
		// LAST_ANALYSIS = areaAnalysisResult;
		// LAST_GAME_AREA_CONFIGURATION = new
		// GameAreaConfiguration(areaAnalysisResult.getArea(), spawnSpots);
		return new GameAreaConfiguration(areaAnalysisResult.getArea(), spawnSpots);
	}

	AreaAnalysisResult findArea(TiledMapCursor cursor, AreaSearchCriteria criteria) {
		StartPositionProvider startPositionProvider = new StartPositionProvider(random);
		while (startPositionProvider.getStep() < MAX_START_POINT_TRY) {
			AreaAnalysisResult result = analyzeArea(cursor, startPositionProvider.next(), criteria.getSquareSize());
			if (criteria.test(result)) {
				return result;
			}
			Log.debug("Area test failed.");
		}
		return null;
	}

	static AreaAnalysisResult analyzeArea(TiledMapCursor cursor, Vector2 startPoint, int maxSquareSize) {
		Position startPosition = Position.fromWorldPosition(startPoint);
		Area area = new Area(startPosition);
		BooleanExtensibleGrid exploredGrid = new BooleanExtensibleGrid();
		for (int i = 0; i < 5; i++) {
			if (!cursor.tileAt(startPosition).isSolid()) {
				break;
			}
			startPosition.addX(1);
		}
		if (cursor.tileAt(startPosition).isSolid()) {
			return new AreaAnalysisResult(area, exploredGrid, POINTS_INTERVAL);
		}
		Queue<Position> explorationQueue = new ArrayDeque<>();
		explorationQueue.add(startPosition);
		while (!explorationQueue.isEmpty()) {
			Position currentPosition = explorationQueue.remove();
			forEachNeighbors(currentPosition, position -> {
				if (!exploredGrid.get(position, POINTS_INTERVAL) && area.enclosingWidth(position) <= maxSquareSize && area.enclosingHeight(position) <= maxSquareSize
						&& isConnected(cursor, currentPosition, position, MAX_FAIL_POSITION_CHECK)) {
					exploredGrid.set(position, POINTS_INTERVAL, true);
					explorationQueue.add(position);
					area.enclose(position);
				}
			});
		}
		return new AreaAnalysisResult(area, exploredGrid, POINTS_INTERVAL);
	}

	private static void forEachNeighbors(Position origin, Consumer<Position> action) {
		action.accept(Position.relativeTo(origin, POINTS_INTERVAL, 0));
		action.accept(Position.relativeTo(origin, 0, POINTS_INTERVAL));
		action.accept(Position.relativeTo(origin, -POINTS_INTERVAL, 0));
		action.accept(Position.relativeTo(origin, 0, -POINTS_INTERVAL));
	}

	/**
	 * Simple pathfinding to check if position1 and position2 are connected by a
	 * simple path.
	 * 
	 * @param cursor
	 *            cursor for accessing tile data
	 * @param position1
	 * @param position2
	 * @param maxFail
	 *            Maximum number of "fail" of the algorithm, a "fail" means "oh,
	 *            hitting a wall, let's change direction"
	 * @return true if the two positions are connected, false if they are
	 *         <b>maybe</b> not connected.
	 */
	static boolean isConnected(TiledMapCursor cursor, Position position1, Position position2, int maxFail) {
		if (cursor.tileAt(position1.getX(), position1.getY()).isSolid() || cursor.tileAt(position2.getX(), position2.getY()).isSolid()) {
			return false;
		}
		int diffX = position2.getX() - position1.getX();
		int diffY = position2.getY() - position1.getY();
		Position currentPosition = position1.copy();
		int failCounter = 0;
		int failingDirection = 0;
		// Loop until the destination point is reached, or to many fails occured
		while ((diffX != 0 || diffY != 0) && failCounter <= maxFail) {
			if (Math.abs(diffX) > Math.abs(diffY)) {
				int stepX = Integer.signum(diffX);
				if (!cursor.tileAt(currentPosition.getX() + stepX, currentPosition.getY()).isSolid()) {
					currentPosition.addX(stepX);
					diffX -= stepX;
					failingDirection = 0;
				} else {
					int stepY = failingDirection == 0 ? notZeroSignum(diffY) : failingDirection;
					if (!cursor.tileAt(currentPosition.getX(), currentPosition.getY() + stepY).isSolid()) {
						currentPosition.addY(stepY);
						diffY -= stepY;
						failingDirection = stepY;
					} else if (!cursor.tileAt(currentPosition.getX(), currentPosition.getY() - stepY).isSolid()) {
						currentPosition.addY(-stepY);
						diffY += stepY;
						failingDirection = -stepY;
					} else {
						return false;
					}
					failCounter++;
				}
			} else {
				int stepY = Integer.signum(diffY);
				if (!cursor.tileAt(currentPosition.getX(), currentPosition.getY() + stepY).isSolid()) {
					currentPosition.addY(stepY);
					diffY -= stepY;
					failingDirection = 0;
				} else {
					int stepX = failingDirection == 0 ? notZeroSignum(diffX) : failingDirection;
					if (!cursor.tileAt(currentPosition.getX() + stepX, currentPosition.getY()).isSolid()) {
						currentPosition.addX(stepX);
						diffX -= stepX;
						failingDirection = stepX;
					} else if (!cursor.tileAt(currentPosition.getX() - stepX, currentPosition.getY()).isSolid()) {
						currentPosition.addX(-stepX);
						diffX += stepX;
						failingDirection = -stepX;
					} else {
						return false;
					}
					failCounter++;
				}
			}
		}
		return failCounter <= maxFail;
	}

	static int notZeroSignum(int i) {
		return i < 0 ? -1 : 1;
	}
}
