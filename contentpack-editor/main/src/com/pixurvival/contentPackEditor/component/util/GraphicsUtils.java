package com.pixurvival.contentPackEditor.component.util;

import lombok.experimental.UtilityClass;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

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
