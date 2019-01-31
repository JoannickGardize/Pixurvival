package com.pixurvival.contentPackEditor.component.valueComponent;

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

	public Bounds(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public static Bounds min(double min) {
		return min(min, false);
	}

	public static Bounds min(double min, boolean exclude) {
		return new Bounds(min, exclude, Double.POSITIVE_INFINITY, false);
	}

	public static Bounds max(double max) {
		return new Bounds(Double.NEGATIVE_INFINITY, false, max, false);
	}

	public static Bounds max(double max, boolean exclude) {
		return new Bounds(Double.NEGATIVE_INFINITY, false, max, exclude);
	}

	public static Bounds none() {
		return new Bounds(Double.NEGATIVE_INFINITY, false, Double.POSITIVE_INFINITY, false);
	}

	public static Bounds positive() {
		return min(0);
	}

	public boolean test(Number value) {
		double doubleValue = value.doubleValue();
		return (!excludeMin && doubleValue >= min || excludeMin && doubleValue > min) && (!excludeMax && doubleValue <= max || excludeMax && doubleValue < max);
	}
}
