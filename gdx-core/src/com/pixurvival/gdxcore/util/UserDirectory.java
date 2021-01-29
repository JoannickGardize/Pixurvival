package com.pixurvival.gdxcore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.util.PropertiesFileUtils;
import com.pixurvival.core.util.WorkingDirectory;
import com.pixurvival.gdxcore.input.InputMapping;
import com.pixurvival.gdxcore.input.InputMappingDefaults;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserDirectory {

	private static final String INPUT_MAPPING_FILE_NAME = "key-config.properties";
	private static final String GENERAL_SETTINGS_FILE_NAME = "general-settings.properties";

	private static @Getter GeneralSettings generalSettings = new GeneralSettings();

	static {
		PropertiesFileUtils.apply(new File(WorkingDirectory.get(), GENERAL_SETTINGS_FILE_NAME), generalSettings);
	}

	public static void saveGeneralSettings() {
		PropertiesFileUtils.save(new File(WorkingDirectory.get(), GENERAL_SETTINGS_FILE_NAME), generalSettings);
	}

	public static InputMapping loadInputMapping() {
		Properties prop = loadPropertiesFile(INPUT_MAPPING_FILE_NAME);
		return prop == null ? InputMappingDefaults.findBestDefaultMatch() : new InputMapping(prop);
	}

	public static void saveInputMapping(InputMapping mapping) {
		savePropertiesFile(INPUT_MAPPING_FILE_NAME, mapping.toProperties());
	}

	private static Properties loadPropertiesFile(String fileName) {
		File file = new File(WorkingDirectory.get(), fileName);
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				Properties properties = new Properties();
				properties.load(fis);
				return properties;
			} catch (IOException e) {
				Log.error("error when loading the file " + file, e);
			}
		}
		return null;
	}

	private static void savePropertiesFile(String fileName, Properties properties) {
		File file = new File(WorkingDirectory.get(), fileName);
		try (FileOutputStream fos = new FileOutputStream(file)) {
			properties.store(fos, null);
		} catch (IOException e) {
			Log.error("error when loading the file " + file, e);
		}
	}
}