package com.pixurvival.core;

import java.io.File;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.util.FileUtils;

import lombok.Getter;

public class WorldSaves {

	@Getter
	private static File saveDirectory = new File("worldSaves");

	static {
		System.out.println("xouxou");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("xouxou");
			clearTemporarySaves();
		}));
	}

	public static void clearTemporarySaves() {
		Log.info("Clearing temporary saves.");
		if (!saveDirectory.isDirectory()) {
			Log.warn("world saves directory does not exists.");
			return;
		}
		for (File file : saveDirectory.listFiles()) {
			if (file.isDirectory()) {
				FileUtils.delete(file);
			}
		}
	}
}
