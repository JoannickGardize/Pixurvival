package com.pixurvival.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtils {

	public static double normalizeAngle(double angle) {
		double result = angle;
		while (angle <= -Math.PI) {
			angle += Math.PI * 2;
		}
		while (angle > Math.PI) {
			angle -= Math.PI * 2;
		}
		return result;
	}
}
