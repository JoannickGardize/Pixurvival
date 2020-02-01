package com.pixurvival.gdxcore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.gdxcore.input.InputMapping;
import com.pixurvival.gdxcore.input.InputMappingDefaults;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserDirectory {

	private static final String INPUT_MAPPING_FILE_NAME = "key-config.properties";

	public static File getUserDirectory() {
		File file = new File(System.getProperty("user.home"), ".pixurvival");
		if (!file.exists()) {
			file.mkdir();
		}
		return file;
	}

	public static InputMapping loadInputMapping() {
		File file = new File(getUserDirectory(), INPUT_MAPPING_FILE_NAME);
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				Properties properties = new Properties();
				properties.load(fis);
				return new InputMapping(properties);
			} catch (IOException e) {
				Log.warn("error when loading the file " + file, e);
			}
		}
		return InputMappingDefaults.findBestDefaultMatch();
	}

	public static void saveInputMapping(InputMapping mapping) {
		File file = new File(getUserDirectory(), INPUT_MAPPING_FILE_NAME);
		Properties properties = mapping.toProperties();
		try (FileOutputStream fos = new FileOutputStream(file)) {
			properties.store(fos, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
