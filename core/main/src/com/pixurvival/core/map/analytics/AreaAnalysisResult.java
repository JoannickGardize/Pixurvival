package com.pixurvival.core.map.analytics;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class AreaAnalysisResult {

	private Area area;
	private BooleanExtensibleGrid freePositions;
	private int pointsInterval;
}
