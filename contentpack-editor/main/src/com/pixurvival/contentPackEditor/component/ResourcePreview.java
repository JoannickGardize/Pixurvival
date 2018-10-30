package com.pixurvival.contentPackEditor.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import lombok.Getter;
import lombok.Setter;

public class ResourcePreview extends JPanel {

	private static final long serialVersionUID = 1L;

	private @Getter Object object;

	private @Getter @Setter boolean spritePreviewEnabled;
	private @Getter int spriteWidth;
	private @Getter int spriteHeight;

	public ResourcePreview(boolean enableSpritePreview) {
		this.spritePreviewEnabled = enableSpritePreview;
		setPreferredSize(new Dimension(400, 300));
	}

	public ResourcePreview() {
		this(false);
	}

	@Override
	public void paint(Graphics g) {
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		((Graphics2D) g).setRenderingHints(rh);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		int xOffset = 0;
		int yOffset = 0;
		int width;
		int height;
		double scale;
		if (object instanceof BufferedImage) {
			BufferedImage image = (BufferedImage) object;
			double hScale = (double) getWidth() / image.getWidth();
			double vScale = (double) getHeight() / image.getHeight();
			g.setColor(Color.BLACK);
			if (vScale < hScale) {
				scale = vScale;
				width = (int) (image.getWidth() * scale);
				height = getHeight();
				xOffset = (getWidth() - width) / 2;
				g.drawImage(image, xOffset, 0, width, height, null);
				g.drawRect(xOffset, 0, width - 1, height - 1);
			} else {
				scale = hScale;
				height = (int) (image.getHeight() * scale);
				width = getWidth();
				yOffset = (getHeight() - height) / 2;
				g.drawImage(image, 0, yOffset, width, height, null);
				g.drawRect(0, yOffset, width - 1, height - 1);
			}
			if (spritePreviewEnabled && spriteWidth > 0 && spriteHeight > 0) {
				for (double x = 0; x < width; x += spriteWidth * scale) {
					g.drawLine((int) x + xOffset, yOffset, (int) x + xOffset, yOffset + height - 1);
				}
				for (double y = 0; y < height; y += spriteHeight * scale) {
					g.drawLine(xOffset, (int) y + yOffset, xOffset + width - 1, (int) y + yOffset);
				}
			}
		}

	}

	public void set(Object o, int spriteWidth, int spriteHeight) {
		this.object = o;
		this.spriteWidth = spriteWidth;
		this.spriteHeight = spriteHeight;
		repaint();
	}

	public void setObject(Object o) {
		this.object = o;
		repaint();
	}

	public void setSpriteWidth(int spriteWidth) {
		this.spriteWidth = spriteWidth;
		repaint();
	}

	public void setSpriteHeight(int spriteHeight) {
		this.spriteHeight = spriteHeight;
		repaint();
	}
}
