package com.pixurvival.contentPackEditor.component;

import com.pixurvival.contentPackEditor.component.util.GraphicsUtils;
import com.pixurvival.contentPackEditor.component.util.LayoutUtils;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ResourcePreview extends JPanel {

    private static final long serialVersionUID = 1L;

    private @Getter
    @Setter Object object;
    private @Getter
    @Setter Rectangle rectangle;

    @Override
    public void paint(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if (object instanceof BufferedImage) {
            BufferedImage image = (BufferedImage) object;
            g.setColor(Color.BLACK);
            Rectangle srcRec = rectangle == null ? new Rectangle(0, 0, image.getWidth(), image.getHeight()) : rectangle;
            Rectangle dstRec = LayoutUtils.getCenteredKeepRatioRectangle(this, srcRec);
            GraphicsUtils.drawImage(g, image, dstRec, srcRec);
            g.drawRect(dstRec.x, dstRec.y, dstRec.width, dstRec.height);
            paintOnTop(g, dstRec);

        }
    }

    protected void paintOnTop(Graphics g, Rectangle rec) {

    }
}
