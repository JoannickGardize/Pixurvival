package com.pixurvival.contentPackEditor.component.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GraphicsUtils {

	public static void drawImage(Graphics g, Image image, Rectangle dstRec, Rectangle srcRec) {
		g.drawImage(image, dstRec.x, dstRec.y, dstRec.x + dstRec.width, dstRec.y + dstRec.height, srcRec.x, srcRec.y,
				srcRec.x + srcRec.width, srcRec.y + srcRec.height, null);
	}

	public static ImageIcon createIcon(Image image) {
		BufferedImage resizedImg = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.drawImage(image, 0, 0, 20, 20, null);
		g2.dispose();
		return new ImageIcon(resizedImg);
	}
}
