package com.pixurvival.contentPackEditor.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import com.pixurvival.contentPackEditor.component.valueComponent.ElementEditor;
import com.pixurvival.contentPackEditor.component.valueComponent.EnumChooser;

public class SettingsEditor extends ElementEditor<Settings> {

	private static final long serialVersionUID = 1L;

	public SettingsEditor() {
		EnumChooser<FontSize> fontSizeChooser = new EnumChooser<>(FontSize.class);

		bind(fontSizeChooser, Settings::getFontSize, Settings::setFontSize);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = LayoutUtils.createGridBagConstraints();
		LayoutUtils.addHorizontalLabelledItem(this, "settings.fontSize", fontSizeChooser, gbc);
	}
}
