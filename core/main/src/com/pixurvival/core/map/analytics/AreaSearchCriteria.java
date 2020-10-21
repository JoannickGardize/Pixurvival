package com.pixurvival.core.map.analytics;

import com.esotericsoftware.minlog.Log;

import lombok.Data;

@Data
public class AreaSearchCriteria {

	private int squareSize = 500;
	private float minFreeArea = 0.4f;
	private float maxFreeArea = 1;
	private int numberOfSpawnSpots = 1;

	public boolean test(AreaAnalysisResult result) {
		Log.debug("width : " + result.getArea().width());
		Log.debug("heigth : " + result.getArea().height());
		if (result.getArea().width() < squareSize - result.getPointsInterval() || result.getArea().height() < squareSize - result.getPointsInterval()) {
			return false;
		}
		float freeArea = computeFreeSpace(result);
		Log.debug("Free Area : " + freeArea);
		return freeArea >= minFreeArea && freeArea <= maxFreeArea;
	}

	public float computeFreeSpace(AreaAnalysisResult result) {
		int pointSideCount = squareSize / result.getPointsInterval() + 1;
		int maximumPointCount = pointSideCount * pointSideCount;
		return (float) result.getFreePositions().size() / maximumPointCount;
	}

}
