package com.pixurvival.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;
import java.util.Properties;

import com.esotericsoftware.minlog.Log;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PropertiesFileUtils {

	public static void apply(File propertiesFile, Object wrapper) {
		if (propertiesFile.exists()) {
			try (FileInputStream fis = new FileInputStream(propertiesFile)) {
				Properties properties = new Properties();
				properties.load(fis);
				for (Entry<Object, Object> entry : properties.entrySet()) {
					try {
						Field field = wrapper.getClass().getDeclaredField((String) entry.getKey());
						ArgsUtils.setValue(wrapper, field, (String) entry.getValue());
					} catch (NoSuchFieldException e) {
						Log.warn("Unexpected properties attribute " + entry.getKey() + ", from the file " + propertiesFile, e);
					}
				}
			} catch (IOException | SecurityException e) {
				Log.warn("Error when loading the file " + propertiesFile, e);
			}
		}
	}

	public static void save(File propertiesFile, Object wrapper) {
		Properties properties = new Properties();
		try (FileOutputStream fos = new FileOutputStream(propertiesFile)) {
			for (Field field : ReflectionUtils.getAllFields(wrapper.getClass())) {
				if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
					field.setAccessible(true);
					properties.put(field.getName(), field.get(wrapper).toString());
				}
			}
			properties.store(fos, null);
		} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
			Log.warn("Error when saving the file " + propertiesFile, e);
		}
	}
}
