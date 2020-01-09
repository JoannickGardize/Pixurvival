package com.pixurvival.core;

import com.pixurvival.core.util.Rectangle;

import lombok.Data;

@Data
public class MapLimitsAnchorRun {

	private long time;
	private Rectangle rectangle;
	private float damagePerSecond;
}
