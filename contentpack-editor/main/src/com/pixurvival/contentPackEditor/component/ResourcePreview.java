package com.pixurvival.contentPackEditor.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import lombok.Getter;

public class ResourcePreview extends JPanel {

	private static final long serialVersionUID = 1L;

	private @Getter Object object;

	public ResourcePreview() {
		setPreferredSize(new Dimension(400, 300));
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		if (object instanceof BufferedImage) {
			BufferedImage image = (BufferedImage) object;
			double hScale = (double) getWidth() / image.getWidth();
			double vScale = (double) getHeight() / image.getHeight();
			g.setColor(Color.BLACK);
			if (vScale < hScale) {
				int width = (int) (image.getWidth() * vScale);
				int x = (getWidth() - width) / 2;
				g.drawImage(image, x, 0, width, getHeight(), null);
				g.drawRect(x, 0, width, getHeight());
			} else {
				int height = (int) (image.getHeight() * hScale);
				int y = (getHeight() - height) / 2;
				g.drawImage(image, 0, y, getWidth(), height, null);
				g.drawRect(0, y, getWidth(), height);
			}
		}
	}

	public void setObject(Object o) {
		this.object = o;
		repaint();
	}

}
