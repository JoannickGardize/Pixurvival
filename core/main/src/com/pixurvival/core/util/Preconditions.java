package com.pixurvival.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Preconditions {

	public static void notEmpty(String value, String fieldName) {
		if (value == null || value.trim().length() == 0) {
			throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
		}
	}
}
