package com.pixurvival.contentPackEditor.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtils {
	public static boolean isValidFilePath(String path) {
		return path.matches("^([-_.A-Za-z0-9]+\\/)*[-_.A-Za-z0-9]+$");
	}
}
