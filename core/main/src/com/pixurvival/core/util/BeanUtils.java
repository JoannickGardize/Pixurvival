package com.pixurvival.core.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BeanUtils {

	public static String upperToCamelCase(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		boolean upper = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '_') {
				upper = true;
			} else {
				if (upper) {
					sb.append(c);
					upper = false;
				} else {
					sb.append(Character.toLowerCase(c));
				}
			}
		}
		return sb.toString();
	}

	public static String camelToUpperCase(String s) {
		StringBuilder sb = new StringBuilder(s.length() + 3);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append('_').append(c);
			} else {
				sb.append(Character.toUpperCase(c));
			}
		}
		return sb.toString();
	}
}
