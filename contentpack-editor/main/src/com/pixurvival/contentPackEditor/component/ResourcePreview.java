package com.pixurvival.contentPackEditor.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.pixurvival.contentPackEditor.component.util.LayoutUtils;

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
			g.setColor(Color.BLACK);
			Rectangle rec = LayoutUtils.getCenteredKeepRatioRectangle(this, image);
			g.drawImage(image, rec.x, rec.y, rec.width, rec.height, null);
			g.drawRect(rec.x, rec.y, rec.width, rec.height);
			paintOnTop(g, rec);

		}

	}

	public void setObject(Object o) {
		this.object = o;
	}

	protected void paintOnTop(Graphics g, Rectangle rec) {

	}
}
