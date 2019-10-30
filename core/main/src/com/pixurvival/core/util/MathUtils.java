package com.pixurvival.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {

	public static final double EPSILON = 0.00001;

	public static boolean equals(double d1, double d2) {
		return Math.abs(d1 - d2) < EPSILON;
	}

	public static double normalizeAngle(double angle) {
		double result = angle;
		while (result <= -Math.PI) {
			result += Math.PI * 2;
		}
		while (result > Math.PI) {
			result -= Math.PI * 2;
		}
		return result;
	}

	public static double clamp(double value, double min, double max) {
		if (value > max) {
			return max;
		} else if (value < min) {
			return min;
		} else {
			return value;
		}
	}

	public static float clamp(float value, float min, float max) {
		if (value > max) {
			return max;
		} else if (value < min) {
			return min;
		} else {
			return value;
		}
	}

	public static double linearInterpolate(double start, double end, double alpha) {
		return start + (end - start) * alpha;
	}

	public static double oppositeDirection(double angle) {
		return angle + Math.PI;
	}

	/**
	 * This method is <b>a lot</b> faster than {@link Math#floor(double)}
	 * 
	 * @param x
	 * @return
	 */
	public static int floor(double x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}

	/**
	 * This method is <b>a lot</b> faster than {@link Math#ceil(double)}
	 * 
	 * @param x
	 * @return
	 */
	public static int ceil(double x) {
		int xi = (int) x;
		return x > xi ? xi + 1 : xi;
	}
}
