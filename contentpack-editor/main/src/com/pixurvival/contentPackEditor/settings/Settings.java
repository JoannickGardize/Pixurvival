package com.pixurvival.contentPackEditor.settings;

import java.awt.Font;
import java.io.File;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.pixurvival.core.util.PropertiesFileUtils;
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
		PropertiesFileUtils.apply(new File(WorkingDirectory.get(), FILE_NAME), this);
	}

	public void save() {
		PropertiesFileUtils.save(new File(WorkingDirectory.get(), FILE_NAME), this);
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
