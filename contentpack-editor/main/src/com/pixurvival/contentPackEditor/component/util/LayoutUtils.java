package com.pixurvival.contentPackEditor.component.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LayoutUtils {

	public static void addHorizontalLabelledItem(Container parent, String labelKey, Component component, GridBagConstraints gbc) {
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

	public static void addHorizontalSeparator(Container parent, GridBagConstraints gbc) {
		gbc.gridwidth = 2;
		parent.add(new JSeparator(), gbc);

		gbc.gridwidth = 1;
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

	public static Border createGroupBorder(String titlekey) {
		return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), TranslationService.getInstance().getString(titlekey));
	}

	public static JPanel createVerticalBox(int gap, Component... components) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = createGridBagConstraints();
		gbc.insets.top = gap;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		for (int i = 0; i < components.length - 1; i++) {
			panel.add(components[i], gbc);
			gbc.gridy++;
		}
		if (components.length > 0) {
			gbc.insets.bottom = gap;
			panel.add(components[components.length - 1], gbc);
		}
		return panel;
	}

	public static Rectangle getCenteredKeepRatioRectangle(Container container, Image image) {
		return getCenteredKeepRatioRectangle(container.getWidth() - 1, container.getHeight() - 1, image.getWidth(null), image.getHeight(null));
	}

	public static Rectangle getCenteredKeepRatioRectangle(int destWidth, int destHeight, int sourceWidth, int sourceHeight) {
		int xOffset = 0;
		int yOffset = 0;
		int width;
		int height;
		double hScale = (double) destWidth / sourceWidth;
		double vScale = (double) destHeight / sourceHeight;
		if (vScale < hScale) {
			width = (int) (sourceWidth * vScale);
			height = destHeight;
			xOffset = (destWidth - width) / 2;
		} else {
			height = (int) (sourceHeight * hScale);
			width = destWidth;
			yOffset = (destHeight - height) / 2;
		}
		return new Rectangle(xOffset, yOffset, width, height);
	}

	public static Function<SpriteSheet, ImageIcon> getSpriteSheetIconProvider() {
		return s -> {
			if (s == null) {
				return null;
			}
			ResourceEntry entry = ResourcesService.getInstance().getResource(s.getImage());
			if (entry != null) {
				return entry.getIcon();
			} else {
				return null;
			}
		};
	}
}
