package com.pixurvival.core.util;

import java.io.File;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkingDirectory {

	public static File get() {
		File file = new File(System.getProperty("user.home"), ".pixurvival");
		if (!file.exists()) {
			file.mkdir();
		}
		return file;
	}
}
