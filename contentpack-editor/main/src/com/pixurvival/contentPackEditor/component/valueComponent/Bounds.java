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
	private float min = Float.NEGATIVE_INFINITY;
	private boolean excludeMin = false;
	private float max = Float.POSITIVE_INFINITY;
	private boolean excludeMax = false;

	public Bounds(float min, float max) {
		this.min = min;
		this.max = max;
	}

	public static Bounds min(float min) {
		return min(min, false);
	}

	public static Bounds min(float min, boolean exclude) {
		return new Bounds(min, exclude, Float.POSITIVE_INFINITY, false);
	}

	public static Bounds max(float max) {
		return new Bounds(Float.NEGATIVE_INFINITY, false, max, false);
	}

	public static Bounds max(float max, boolean exclude) {
		return new Bounds(Float.NEGATIVE_INFINITY, false, max, exclude);
	}

	public static Bounds none() {
		return new Bounds(Float.NEGATIVE_INFINITY, false, Float.POSITIVE_INFINITY, false);
	}

	public static Bounds positive() {
		return min(0);
	}

	public boolean test(Number value) {
		float floatValue = value.floatValue();
		return (!excludeMin && floatValue >= min || excludeMin && floatValue > min) && (!excludeMax && floatValue <= max || excludeMax && floatValue < max);
	}
}
