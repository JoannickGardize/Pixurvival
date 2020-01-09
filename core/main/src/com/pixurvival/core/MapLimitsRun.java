package com.pixurvival.core;

import com.pixurvival.core.util.Rectangle;

import lombok.Data;

@Data
public class MapLimitsRun {

	private Rectangle rectangle;
	private float trueDamagePerSecond;
	private MapLimitsAnchorRun from;
	private MapLimitsAnchorRun to;
}
