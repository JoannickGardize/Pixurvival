package com.pixurvival.contentPackEditor.component.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.pixurvival.contentPackEditor.ResourceEntry;
import com.pixurvival.contentPackEditor.ResourcesService;
import com.pixurvival.contentPackEditor.TranslationService;
import com.pixurvival.contentPackEditor.component.valueComponent.ValueComponent;
import com.pixurvival.core.contentPack.sprite.SpriteSheet;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LayoutUtils {

	public static final int NORMAL_GAP = 5;

	public static void addHorizontalLabelledItem(Container parent, String labelKey, Component component, GridBagConstraints gbc) {
		addHorizontalLabelledItem(parent, labelKey, null, component, gbc);
	}

	public static void addHorizontalLabelledItem(Container parent, String labelKey, String toolTipKey, Component component, GridBagConstraints gbc) {
		int previousAnchor = gbc.anchor;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		JLabel label = new JLabel(TranslationService.getInstance().getString(labelKey));
		parent.add(label, gbc);
		gbc.anchor = previousAnchor;
		gbc.gridx++;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		int previousLeft = gbc.insets.left;
		gbc.insets.left = NORMAL_GAP;
		parent.add(component, gbc);
		gbc.insets.left = previousLeft;
		if (component instanceof ValueComponent) {
			((ValueComponent<?>) component).setAssociatedLabel(label);
		}
		gbc.gridx--;
		gbc.gridy++;

		if (toolTipKey != null) {
			label.setToolTipText(TranslationService.getInstance().getString(toolTipKey));
		}
	}

	public static void nextColumn(GridBagConstraints gbc) {
		gbc.gridx += 2;
		gbc.gridy = 0;
		gbc.insets.left = NORMAL_GAP;
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

	public static void addSideBySide(Container parent, Component left, Component right) {
		parent.setLayout(new GridBagLayout());
		GridBagConstraints gbc = createGridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		parent.add(left, gbc);
		gbc.gridx++;
		parent.add(right, gbc);

	}

	public static JPanel labelled(String labelKey, Component component) {
		JPanel panel = new JPanel(new BorderLayout(NORMAL_GAP, 0));
		JLabel label = new JLabel(TranslationService.getInstance().getString(labelKey));
		panel.add(label, BorderLayout.WEST);
		panel.add(component, BorderLayout.CENTER);
		if (component instanceof ValueComponent) {
			((ValueComponent<?>) component).setAssociatedLabel(label);
		}
		return panel;
	}

	public static Border createGroupBorder(String titlekey) {
		return BorderFactory.createTitledBorder(createBorder(), TranslationService.getInstance().getString(titlekey));
	}

	public static Border createBorder() {
		return BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
	}

	public static JPanel createHorizontalBox(int gap, Component... components) {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = createGridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets.right = gap;
		gbc.insets.left = gap;
		for (int i = 0; i < components.length; i++) {
			panel.add(components[i], gbc);
			gbc.gridx++;
			gbc.insets.left = 0;
		}
		return panel;
	}

	public static JPanel createHorizontalBox(Component... components) {
		return createHorizontalBox(NORMAL_GAP, components);
	}

	public static JPanel createVerticalBox(int gap, Component... components) {
		return createVerticalBox(gap, -1, components);
	}

	public static JPanel createVerticalBox(int gap, int fillIndex, Component... components) {
		JPanel panel = new JPanel();
		addVertically(panel, gap, fillIndex, components);
		return panel;
	}

	public static void addVertically(JPanel panel, int gap, Component... components) {
		addVertically(panel, gap, -1, components);
	}

	public static void addVertically(JPanel panel, Component... components) {
		addVertically(panel, NORMAL_GAP, components);
	}

	public static void addVertically(JPanel panel, int gap, int fillIndex, Component... components) {
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = createGridBagConstraints();
		gbc.insets.top = gap;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		for (int i = 0; i < components.length; i++) {
			if (fillIndex == i) {
				gbc.weighty = 1;
			}
			if (i == components.length - 1) {
				gbc.insets.bottom = gap;
			}
			panel.add(components[i], gbc);
			gbc.weighty = 0;
			gbc.gridy++;
		}
	}

	public static Rectangle getCenteredKeepRatioRectangle(Container container, Rectangle rectangle) {
		return getCenteredKeepRatioRectangle(container.getWidth() - 1, container.getHeight() - 1, (int) rectangle.getWidth(), (int) rectangle.getHeight());
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

	public static Function<SpriteSheet, Icon> getSpriteSheetIconProvider() {
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

	public static void setMinimumSize(Component component, int minimumWidth, int minimumHeight) {
		Dimension dim = new Dimension(minimumWidth, minimumHeight);
		component.setMinimumSize(dim);
		component.setPreferredSize(dim);
	}

	public static Component addBorder(Component comp, int top, int left, int bottom, int right) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(comp, BorderLayout.CENTER);
		panel.setBorder(new EmptyBorder(top, left, bottom, right));
		return panel;
	}

	public static <T extends JComponent> Supplier<T> bordered(Supplier<T> supplier) {
		return () -> {
			T result = supplier.get();
			result.setBorder(createBorder());
			return result;
		};
	}
}