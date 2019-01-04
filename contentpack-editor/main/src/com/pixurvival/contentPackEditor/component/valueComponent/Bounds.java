package com.pixurvival.contentPackEditor.component.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bounds {
	private double min = Double.NEGATIVE_INFINITY;
	private boolean excludeMin = false;
	private double max = Double.POSITIVE_INFINITY;
	private boolean excludeMax = false;

	public static Bounds minBounds(double min) {
		return minBounds(min, false);
	}

	public static Bounds minBounds(double min, boolean exclude) {
		return new Bounds(min, exclude, Double.POSITIVE_INFINITY, false);
	}

	public static Bounds maxBounds(double max) {
		return new Bounds(Double.NEGATIVE_INFINITY, false, max, false);
	}

	public static Bounds maxBounds(double max, boolean exclude) {
		return new Bounds(Double.NEGATIVE_INFINITY, false, max, exclude);
	}

	public boolean test(double value) {
		return (!excludeMin && value >= min || excludeMin && value > min) && (!excludeMax && value <= max || excludeMax && value < max);
	}
}
