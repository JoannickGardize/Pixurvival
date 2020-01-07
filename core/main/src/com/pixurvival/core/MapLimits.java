package com.pixurvival.core;

import com.pixurvival.core.util.Rectangle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapLimits {

	public static final MapLimits NO_LIMITS = new MapLimits(new Rectangle(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY), 0);

	private Rectangle rectangle;
	private float trueDamagePerSecond;
}
