package com.pixurvival.contentPackEditor.component.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.TranslationService;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LayoutUtils {

	public static void addHorizontalLabelledItem(Container parent, String labelKey, Component component,
			GridBagConstraints gbc) {
		int previousAnchor = gbc.anchor;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		JLabel label = new JLabel(TranslationService.getInstance().getString(labelKey));
		parent.add(label, gbc);
		gbc.anchor = previousAnchor;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.BOTH;
		int previousLeft = gbc.insets.left;
		gbc.insets.left = 5;
		parent.add(component, gbc);
		gbc.insets.left = previousLeft;
		if (component instanceof ValueComponent) {
			((ValueComponent<?>) component).setAssociatedLabel(label);
		}
		gbc.gridx--;
		gbc.gridy++;
	}

	public static void addEmptyFiller(Container parent, GridBagConstraints gbc) {
		gbc.gridwidth = 2;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		parent.add(new JPanel(), gbc);
	}

	public static GridBagConstraints createGridBagConstraints() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		return gbc;
	}

}
