package com.pixurvival.contentPackEditor.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.elementEditor.ElementEditor;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;

public class SettingsEditor extends ElementEditor<Settings> {

	private static final long serialVersionUID = 1L;

	public SettingsEditor() {
		super(Settings.class);
		EnumChooser<FontSize> fontSizeChooser = new EnumChooser<>(FontSize.class);
		EnumChooser<Skin> skinChooser = new EnumChooser<>(Skin.class);

		bind(fontSizeChooser, "fontSize");
		bind(skinChooser, "skin");

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "settings.fontSize", fontSizeChooser, gbc);
		LayoutUtils.addHorizontalLabelledItem(this, "settings.skin", skinChooser, gbc);
	}
}
