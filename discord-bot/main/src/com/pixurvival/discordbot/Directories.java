package com.pixurvival.discordbot;

import java.io.File;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Directories {

	public static File tmp() {
		return get("tmp");
	}

	public static File contentPacks() {
		return get("contentPacks");
	}

	private static File get(String name) {
		File file = new File(name);
		if (!file.exists()) {
			file.mkdir();
		}
		return file;
	}
}
