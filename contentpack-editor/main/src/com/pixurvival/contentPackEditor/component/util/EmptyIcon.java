package com.pixurvival.contentPackEditor.component.util;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyIcon implements Icon {

	public static final EmptyIcon INSTANCE = new EmptyIcon();

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {

		// Nothing to paint
	}

	@Override
	public int getIconWidth() {
		return 0;
	}

	@Override
	public int getIconHeight() {
		return 0;
	}

}
