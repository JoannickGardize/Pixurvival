package com.pixurvival.contentPackEditor.settings;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.esotericsoftware.minlog.Log;
import com.pixurvival.core.util.ArgsUtils;
import com.pixurvival.core.util.ReflectionUtils;
import com.pixurvival.core.util.WorkingDirectory;

import lombok.Getter;
import lombok.Setter;

/**
 * Settings of the Content Pack Editor
 * 
 * @author SharkHendrix
 *
 */
@Getter
@Setter
public class Settings {

	public static final String FILE_NAME = "editor-settings.properties";

	private static final @Getter Settings instance = new Settings();

	private FontSize fontSize = FontSize.NORMAL;

	private Skin skin = Skin.SYSTEM_DEFAULT;

	public Settings() {
		File file = new File(WorkingDirectory.get(), FILE_NAME);
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file)) {
				Properties properties = new Properties();
				properties.load(fis);
				for (Entry<Object, Object> entry : properties.entrySet()) {
					Field field = Settings.class.getDeclaredField((String) entry.getKey());
					ArgsUtils.setValue(this, field, (String) entry.getValue());
				}
			} catch (IOException | NoSuchFieldException | SecurityException e) {
				Log.warn("Error when loading the file " + file, e);
			}
		}
	}

	public void save() {
		File file = new File(WorkingDirectory.get(), FILE_NAME);
		Properties properties = new Properties();
		try (FileOutputStream fos = new FileOutputStream(file)) {
			for (Field field : ReflectionUtils.getAllFields(Settings.class)) {
				if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
					properties.put(field.getName(), field.get(this).toString());
				}
			}
			properties.store(fos, null);
		} catch (IOException | IllegalArgumentException | IllegalAccessException e) {
			Log.warn("Error when saving the file " + file, e);
		}
	}

	/**
	 * @param appFrame
	 *            the frame of the current swing application, if any.
	 */
	public void apply(JFrame appFrame) {
		skin.getLookAndFeelApplier().run();
		applyFontSize(fontSize.getMultiplier());
		if (appFrame != null) {
			SwingUtilities.updateComponentTreeUI(appFrame);
		}
	}

	private static void applyFontSize(float factor) {
		for (Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
			UIManager.put(entry.getKey(), entry.getValue());
		}
		Set<Object> keySet = UIManager.getLookAndFeelDefaults().keySet();
		Object[] keys = keySet.toArray(new Object[keySet.size()]);
		for (Object key : keys) {
			if (key != null && key.toString().toLowerCase().contains("font")) {
				Font font = UIManager.getDefaults().getFont(key);
				if (font != null) {
					font = font.deriveFont(font.getSize() * factor);
					UIManager.put(key, font);
				}
			}
		}
	}
}
